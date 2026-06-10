package com.Vulcao.Auction.repositorys;

import com.Vulcao.Auction.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBidRepository extends JpaRepository<Bid, UUID> {

    List<Bid> findByUser_IdUsuario(UUID idUsuario);
    List<Bid> findByAuction_IdAuction(UUID idAuction);

}
