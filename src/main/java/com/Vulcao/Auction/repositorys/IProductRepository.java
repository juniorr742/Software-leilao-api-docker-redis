package com.Vulcao.Auction.repositorys;

import com.Vulcao.Auction.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Pruduct p JOIN Auction a on a.product = p WHERE a.idAuction = :idAuction")
    Optional<Product> findByAuctionId(@Param("idAuction") UUID idAuction);
}
