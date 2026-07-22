package com.Vulcao.Auction.repositorys;

import com.Vulcao.Auction.model.Auction;
import com.Vulcao.Auction.model.enums.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAuctionRepository extends JpaRepository<Auction, UUID> {
    Optional<Auction> findByProduct_IdProduct(UUID idProduct);

    List<Auction> findByProduct_Owner_IdUsuario(UUID idUsuario);

    @Query("SELECT a FROM Auction a WHERE a.status = :status AND a.finishAction <= :now")
    List<Auction> findAuctionToFinalize(@Param("now") LocalDateTime now, @Param("status") AuctionStatus status);
}
