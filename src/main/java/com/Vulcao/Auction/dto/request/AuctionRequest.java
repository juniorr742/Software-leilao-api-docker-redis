package com.Vulcao.Auction.dto.request;


import com.Vulcao.Auction.model.enums.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionRequest(
        UUID idProduct,
        BigDecimal actualPrice,
        LocalDateTime startAction,
        LocalDateTime finishAction,
        AuctionStatus status) {
}
