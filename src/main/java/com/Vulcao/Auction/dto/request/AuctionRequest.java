package com.Vulcao.Auction.dto.request;

import com.Vulcao.Auction.model.enums.AuctionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionRequest(UUID idAuction,
                             UUID idProduct,
                             LocalDateTime startAction,
                             LocalDateTime finishAction,
                             AuctionStatus status) {
}
