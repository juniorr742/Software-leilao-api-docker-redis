package com.Vulcao.Auction.repositorys;

import com.Vulcao.Auction.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IAuctionRepository extends JpaRepository<Auction, UUID> {
    Optional<Auction> findByProduct_IdProduct(UUID idProduct);

    Optional<Auction> findByProduct_Owner_IdUsuario(UUID idUsuario);
}
