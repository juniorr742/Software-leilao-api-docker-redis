package com.Vulcao.Auction.repositorys;

import com.Vulcao.Auction.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {

    // 1. Buscar usuário por lanceId
    // Caminho: User -> Bids -> idBid
    Optional<User> findByBids_IdBid(UUID idBid);

    // 2. Buscar usuário por productId (Dono do produto)
    // Caminho: User -> Products -> idProduct
    Optional<User> findByProducts_IdProduct(UUID idProduct);

    // 3. Buscar usuário por auctionId (Dono do produto que está no leilão)
    // Como o leilão aponta para o produto e o produto para o usuário, 
    // usamos uma query JPQL para facilitar o caminho inverso.
    @Query("SELECT u FROM User u JOIN u.products p JOIN Auction a ON a.product = p WHERE a.idAuction = :idAuction")
    Optional<User> findByAuctionId(@Param("idAuction") UUID idAuction);
}
