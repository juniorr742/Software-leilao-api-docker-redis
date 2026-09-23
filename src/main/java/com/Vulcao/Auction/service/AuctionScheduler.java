package com.Vulcao.Auction.service;

import com.Vulcao.Auction.model.Auction;
import com.Vulcao.Auction.model.enums.AuctionStatus;
import com.Vulcao.Auction.repositorys.IAuctionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class AuctionScheduler {

    @Autowired
    private IAuctionRepository auctionRepository;

    @Autowired
    private AuctionService auctionService;

    @Scheduled(fixedDelay = 10000)
    public void finishAuction(){
        LocalDateTime agora = LocalDateTime.now();

        List<Auction> finish = auctionRepository.findAuctionToFinalize(agora, AuctionStatus.ATIVO);

        if (!finish.isEmpty()){
            log.info("Encontrados {} leilões para finalizar...", finish.size());

            auctionService.finishAuctions(finish);
        }
    }
}
