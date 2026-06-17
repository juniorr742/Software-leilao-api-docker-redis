package com.Vulcao.Auction.dto.response;

import com.Vulcao.Auction.model.Product;
import com.Vulcao.Auction.model.enums.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionResponse(
        UUID id,
        ProductResponse product,
        BigDecimal price,
        LocalDateTime startAction,
        LocalDateTime finishAction,
        AuctionStatus status
        ) {
}
