package com.Vulcao.Auction.service;

import com.Vulcao.Auction.model.Auction;
import com.Vulcao.Auction.model.Bid;
import com.Vulcao.Auction.model.User;
import com.Vulcao.Auction.repositorys.IAuctionRepository;
import com.Vulcao.Auction.repositorys.IBidRepository;
import com.Vulcao.Auction.repositorys.IUserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class BidQueueConsumer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IBidRepository bidRepository;

    @Autowired
    private IAuctionRepository auctionRepository;

    @Autowired
    private IUserRepository userRepository;

    @PostConstruct
    public void startConsumer(){
        new Thread(this::consumeQueue).start();
    }

    private void consumeQueue(){
        while (true){
            try {
                String payload = redisTemplate.opsForList().rightPop("queue:bids", Duration.ofSeconds(10));

                if (payload != null){
                    String[] parts = payload.split(";");
                    UUID auctionId = UUID.fromString(parts[0]);
                    UUID userId = UUID.fromString(parts[1]);
                    BigDecimal value = new BigDecimal(parts[2]);

                    Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new EntityNotFoundException("Leilão não encontrado"));
                    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

                    Bid bid = Bid.builder()
                            .auction(auction)
                            .user(user)
                            .timestamp(LocalDateTime.now())
                            .value(value).build();

                    bidRepository.save(bid);
                }
            } catch (Exception e){
               throw new RuntimeException("Erro ao processar lance da fila: " + e.getMessage());
            }
        }
    }
}
