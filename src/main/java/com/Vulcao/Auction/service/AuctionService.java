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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionService {

    @Autowired
    IAuctionRepository auctionRepository;

    @Autowired
    IProductRepository productRepository;

    @Autowired
    RedisTemplate redisTemplate;

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

    public AuctionResponse auctionForProduct(UUID idProduct){
        Auction auction = auctionRepository.findByProduct_IdProduct(idProduct).orElseThrow(() -> new EntityNotFoundException("Leilão não encontrado"));

        return toResponse(auction);
    }

    public List<AuctionResponse> listAuctionForUser(UUID idUser){
        List<AuctionResponse> list = auctionRepository.findByProduct_Owner_IdUsuario(idUser).stream().map(resp ->
                new AuctionResponse(
                        resp.getIdAuction(),
                        resp.getProduct().getIdProduct(),
                        resp.getActualPrice(),
                        resp.getStartAction(),
                        resp.getFinishAction(),
                        resp.getStatus()
                )).toList();

        return list;
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
    public void finishAuctions(List<Auction> leiloesExpirados){
        for (Auction fin: leiloesExpirados){
            String redisKey = "auction:"+fin.getIdAuction()+":highest";
            Map<Object, Object> highest = redisTemplate.opsForHash().entries(redisKey);

            if (highest.isEmpty()) {
                fin.setStatus(AuctionStatus.FINALIZADO);
                continue;
            }

            Map.Entry<Object, Object> entry = highest.entrySet().iterator().next();
            UUID userId = UUID.fromString(entry.getKey().toString());
            BigDecimal valorLance = new BigDecimal(entry.getValue().toString());

            User winner = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

            if (winner != null) {
                if (winner.getSaldo().compareTo(valorLance) < 0){
                    throw new RuntimeException("Saldo insuficiente");
                }

                winner.setSaldo(winner.getSaldo().subtract(valorLance));
                fin.setWinner(winner);
                fin.setStatus(AuctionStatus.FINALIZADO);
                redisTemplate.delete(redisKey);
            }
        }
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

        AuctionResponse auctionResponse = new AuctionResponse(auction.getIdAuction(),
                auction.getProduct().getIdProduct(),
                auction.getActualPrice(),
                auction.getStartAction(),
                auction.getFinishAction(),
                auction.getStatus());

        return auctionResponse;
    }
}
