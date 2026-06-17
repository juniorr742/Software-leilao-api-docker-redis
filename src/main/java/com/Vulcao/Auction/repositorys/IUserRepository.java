package com.Vulcao.Auction.repositorys;

import com.Vulcao.Auction.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {


    Optional<User> findByBids_IdBid(UUID idBid);

    Optional<User> findByProducts_IdProduct(UUID idProduct);

    @Query("SELECT u FROM User u JOIN u.products p JOIN Auction a ON a.product = p WHERE a.idAuction = :idAuction")
    Optional<User> findByAuctionId(@Param("idAuction") UUID idAuction);
}
