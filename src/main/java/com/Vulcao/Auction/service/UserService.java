package com.Vulcao.Auction.service;

import com.Vulcao.Auction.dto.request.UserRequest;
import com.Vulcao.Auction.dto.response.BidResponse;
import com.Vulcao.Auction.dto.response.ProductResponse;
import com.Vulcao.Auction.dto.response.UserResponse;
import com.Vulcao.Auction.model.Product;
import com.Vulcao.Auction.model.User;
import com.Vulcao.Auction.model.enums.UserStatus;
import com.Vulcao.Auction.repositorys.IUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    IUserRepository userRepository;

    public UserResponse createUser(UserRequest request){
        if (userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email já existente");
        }

        User user = User.builder().name(request.name()).email(request.email()).password(request.password())
                .saldo(request.saldo()).status(UserStatus.USER).products(new ArrayList<>()).bids(new ArrayList<>()).build();
        return toResponse(user);
    }

    public UserResponse searchByBids(UUID idBid){
        User user = userRepository.findByBids_IdBid(idBid).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return toResponse(user);
    }

    public UserResponse searchByProducts(UUID idProduct){
        User user = userRepository.findByProducts_IdProduct(idProduct).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return toResponse(user);
    }

    public UserResponse searchByAuction(UUID idAuction){
        User user = userRepository.findByAuctionId(idAuction).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return toResponse(user);
    }

    public UserResponse updateUser(UUID idUser, UserRequest request){
        User user = userRepository.findById(idUser).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        user.setName(request.name());
        if (userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email já existente");
        }else {
            user.setEmail(request.email());
        }
    }

    private UserResponse toResponse(User user){
        List<ProductResponse> listProducts = user.getProducts().stream().map(res -> new ProductResponse(
                res.getIdProduct(),
                res.getOwner().getIdUsuario(),
                res.getName(),
                res.getPrice()
        )).toList();

        List<BidResponse> listBids = user.getBids().stream().map(res -> new BidResponse(
                res.getIdBid(),
                res.getAuction().getIdAuction(),
                res.getUser().getIdUsuario(),
                res.getValue(),
                res.getTimestamp()
        )).toList();

        return new UserResponse(
                user.getIdUsuario(),
                user.getName(),
                listProducts,
                listBids
        );
    }
}
