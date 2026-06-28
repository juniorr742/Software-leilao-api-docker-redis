package com.Vulcao.Auction.service;

import com.Vulcao.Auction.dto.request.AuctionRequest;
import com.Vulcao.Auction.dto.request.BidRequest;
import com.Vulcao.Auction.dto.response.AuctionResponse;
import com.Vulcao.Auction.dto.response.BidResponse;
import com.Vulcao.Auction.dto.response.PageResponse;
import com.Vulcao.Auction.dto.response.ProductResponse;
import com.Vulcao.Auction.model.Auction;
import com.Vulcao.Auction.model.Bid;
import com.Vulcao.Auction.model.Product;
import com.Vulcao.Auction.model.User;
import com.Vulcao.Auction.model.enums.AuctionStatus;
import com.Vulcao.Auction.repositorys.IAuctionRepository;
import com.Vulcao.Auction.repositorys.IBidRepository;
import com.Vulcao.Auction.repositorys.IProductRepository;
import com.Vulcao.Auction.repositorys.IUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionService {

    @Autowired
    IAuctionRepository auctionRepository;

    @Autowired
    IProductRepository productRepository;

    @Autowired
    IBidRepository bidRepository;

    @Autowired
    IUserRepository userRepository;

    public PageResponse<Auction> auctionPages(int pages){
        Pageable pageable = PageRequest.of(pages, 100);
        Page<Auction> auctionPage = auctionRepository.findAll(pageable);

        return PageResponse.<Auction>builder().conteudo(auctionPage.getContent()).totalElementos(auctionPage.getTotalElements()).
                totalPaginas(auctionPage.getTotalPages()).paginaAtual(auctionPage.getNumber()).tamanhoPagina(auctionPage.getSize()).build();
    }

    public AuctionResponse createAuction(AuctionRequest request){
        Product product = productRepository.findById(request.idProduct()).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        Auction auction = Auction.builder()
                .product(product)
                .actualPrice(request.actualPrice())
                .startAction(request.startAction())
                .finishAction(request.finishAction())
                .status(request.status()).build();

        auctionRepository.save(auction);
        return toResponse(auction);
    }

    @Transactional
    public void createBid(BidRequest request){
        Auction auction = auctionRepository.findById(request.idAuction()).orElseThrow(() -> new EntityNotFoundException("Leilão não encontrado"));
        User user = userRepository.findById(request.idUser()).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        Bid bid = Bid.builder()
                .auction(auction)
                .user(user)
                .value(request.value())
                .timestamp(request.timestamp())
                .build();

        auction.getBids().add(bid);
        bidRepository.save(bid);
    }

    public AuctionResponse updateAucition(UUID idAuction, AuctionRequest request){
        Auction auction = auctionRepository.findById(idAuction).orElseThrow(() -> new EntityNotFoundException("Leilão não encontrado"));
        Product product = productRepository.findById(request.idProduct()).orElseThrow(() -> new EntityNotFoundException("produto não encontrado"));

        auction.setActualPrice(request.actualPrice());
        auction.setProduct(product);
        auction.setStatus(request.status());
        auction.setStartAction(request.startAction());
        auction.setFinishAction(request.finishAction());

        auctionRepository.save(auction);
        return toResponse(auction);
    }

    public AuctionResponse cancelAuction(UUID idAuction){
        Auction auction = auctionRepository.findById(idAuction).orElseThrow(() -> new EntityNotFoundException("Leilão não encontrado"));
        auction.setStatus(AuctionStatus.CANCELADO);
        return toResponse(auctionRepository.save(auction));
    }

    public AuctionResponse toResponse(Auction auction){

        ProductResponse productResponse = new ProductResponse(auction.getProduct().getIdProduct(),
                auction.getProduct().getOwner().getIdUsuario(),
                auction.getProduct().getName(),
                auction.getProduct().getPrice());

        AuctionResponse auctionResponse = new AuctionResponse(auction.getIdAuction(),
                productResponse,
                auction.getActualPrice(),
                auction.getStartAction(),
                auction.getFinishAction(),
                auction.getStatus());

        return auctionResponse;
    }
}
