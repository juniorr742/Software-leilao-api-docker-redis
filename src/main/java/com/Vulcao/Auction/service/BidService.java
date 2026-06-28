package com.Vulcao.Auction.service;

import com.Vulcao.Auction.dto.request.BidRequest;
import com.Vulcao.Auction.dto.response.*;
import com.Vulcao.Auction.model.Auction;
import com.Vulcao.Auction.model.Bid;
import com.Vulcao.Auction.model.enums.AuctionStatus;
import com.Vulcao.Auction.repositorys.IAuctionRepository;
import com.Vulcao.Auction.repositorys.IBidRepository;
import com.Vulcao.Auction.repositorys.IUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BidService {

    @Autowired
    IBidRepository bidRepository;

    @Autowired
    IAuctionRepository auctionRepository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedisScript<Long> validateBidScript;

    public PageResponse<Bid> auctionPages(int pages){
        Pageable pageable = PageRequest.of(pages, 100);
        Page<Bid> bidPage = bidRepository.findAll(pageable);

        return PageResponse.<Bid>builder().conteudo(bidPage.getContent()).totalElementos(bidPage.getTotalElements()).
                totalPaginas(bidPage.getTotalPages()).paginaAtual(bidPage.getNumber()).tamanhoPagina(bidPage.getSize()).build();
    }

    public BidResponse createBid(BidRequest request){
        String auctionKey = "auction:" + request.idAuction() + ":highest";

        Long result = redisTemplate.execute(
                validateBidScript,
                List.of(auctionKey),
                request.value().toString(),
                request.idUser().toString()
        );

        if (result == null || result == 0) {
            throw new IllegalArgumentException("O valor do lance é menor ou igual ao lance atual!");
        }

        String queuePayload = request.idAuction() + ";" + request.idUser() + ";" + request.value();

        redisTemplate.opsForList().leftPush("queue:bids", queuePayload);

        return new BidResponse((UUID) null, (UUID) null, (UUID) null, request.value(), LocalDateTime.now());
    }

    @Transactional
    public void cancelBid(UUID auctionId, UUID userId, BigDecimal value){

    Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new EntityNotFoundException("Leilão não encontrado"));

    if (auction.getStatus() == AuctionStatus.FINALIZADO || auction.getFinishAction() != null && auction.getFinishAction().isBefore(LocalDateTime.now())){
        throw new IllegalArgumentException("Não é possível cancelar um lance de um leilão já encerrado");
    }

    String queuePayLoad = auctionId + ";" + userId + ";" + value;

    Long removedCount = redisTemplate.opsForList().remove("queue:bids", 0, queuePayLoad);

    Optional<Bid> bidInDb = bidRepository.findFirstByAuction_IdAuctionAndUser_IdUsuarioAndValue(auctionId, userId, value);
    if (bidInDb.isPresent()){
        bidRepository.delete(bidInDb.get());
    }

    String auctionKey = "auction:" + auctionId + ":highest";
    String cachedHighestUser = (String) redisTemplate.opsForHash().get(auctionKey, "userId");
    String cachedHighestValueStr = (String) redisTemplate.opsForHash().get(auctionKey, "value");

    if (cachedHighestUser != null && cachedHighestValueStr != null){
        BigDecimal cacheHighestValue = new BigDecimal(cachedHighestValueStr);
        if (cacheHighestValue.compareTo(value) == 0 && cachedHighestUser.equals(userId.toString())){
            Optional<Bid> newHighestBid = bidRepository.findTopByAuction_IdAuctionOrderByValueDesc(auctionId);

            if (newHighestBid.isPresent()){
                redisTemplate.opsForHash().put(auctionKey, "value", newHighestBid.get().getValue().toString());
                redisTemplate.opsForHash().put(auctionKey, "userId", newHighestBid.get().getUser().getIdUsuario());
            }else {
                redisTemplate.delete(auctionKey);
            }
        }
    }

    }

    public BidResponse toResponse(Bid bid){

        return new BidResponse(
                bid.getIdBid(),
                bid.getAuction().getIdAuction(),
                bid.getUser().getIdUsuario(),
                bid.getValue(),
                bid.getTimestamp()
        );
    }
}
