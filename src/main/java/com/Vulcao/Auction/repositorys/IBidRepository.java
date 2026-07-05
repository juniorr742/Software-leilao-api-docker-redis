package com.Vulcao.Auction.repositorys;

import com.Vulcao.Auction.model.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IBidRepository extends JpaRepository<Bid, UUID> {

    Page<Bid> findByUser_IdUsuario(Pageable pageable, UUID idUsuario);
    Page<Bid> findByAuction_IdAuction(Pageable pageable, UUID idAuction);

    Optional<Bid> findFirstByAuction_IdAuctionAndUser_IdUsuarioAndValue(UUID idAuction, UUID idUsuario, BigDecimal value);

    Optional<Bid> findTopByAuction_IdAuctionOrderByValueDesc(UUID idAuction);
}
