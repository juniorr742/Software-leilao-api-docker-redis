package com.Vulcao.Auction.service;

import com.Vulcao.Auction.dto.request.ProductRequest;
import com.Vulcao.Auction.dto.response.PageResponse;
import com.Vulcao.Auction.dto.response.ProductResponse;
import com.Vulcao.Auction.model.Product;
import com.Vulcao.Auction.model.User;
import com.Vulcao.Auction.repositorys.IProductRepository;
import com.Vulcao.Auction.repositorys.IUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
public class ProductService {

    @Autowired
    IProductRepository productRepository;

    @Autowired
    IUserRepository userRepository;


    public ProductResponse createProduct(ProductRequest request){
        User user = userRepository.findById(request.idUser()).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

            Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .owner(user).build();

            productRepository.save(product);
            user.getProducts().add(product);

        return toResponse(product);
    }

    public ProductResponse editProduct(UUID idProduct, ProductRequest request){
        Product product = productRepository.findById(idProduct).orElseThrow(() -> new EntityNotFoundException("Produto nã encontrado"));
        User user = userRepository.findById(request.idUser()).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        product.setDescription(request.description());
        product.setName(request.name());
        product.setOwner(user);
        product.setPrice(request.price());

        productRepository.save(product);

        return toResponse(product);
    }

    public boolean deleteProduct(UUID idProduct){
        Product product = productRepository.findById(idProduct).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        User user = userRepository.findById(product.getOwner().getIdUsuario()).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (product.getOwner().getIdUsuario() == user.getIdUsuario()){
            throw new RuntimeException("Só é permitido deletar um produto seu");
        }

        userRepository.deleteById(idProduct);

        return true;
    }

    public PageResponse<Product> listProducts(int pages){
        Pageable pageable = PageRequest.of(pages, 100);
        Page<Product> productPage = productRepository.findAll(pageable);

        return PageResponse.<Product>builder().conteudo(productPage.getContent()).totalElementos(productPage.getTotalElements()).
                totalPaginas(productPage.getTotalPages()).paginaAtual(productPage.getNumber()).tamanhoPagina(productPage.getSize()).build();
    }

    public ProductResponse productForAuction(UUID idAuction){
        Product product = productRepository.findByAuctionId(idAuction).orElseThrow(() -> new EntityNotFoundException("produto não encontrado"));

        return toResponse(product);
    }

    private ProductResponse toResponse(Product product){
              return new ProductResponse(
                product.getIdProduct(),
                product.getOwner().getIdUsuario(),
                product.getName(),
                product.getPrice()
        );
    }
}
