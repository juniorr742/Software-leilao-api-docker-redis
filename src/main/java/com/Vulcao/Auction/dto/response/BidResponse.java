package com.Vulcao.Auction.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BidResponse(UUID idBid,
                          AuctionResponse auction,
                          UserResponse owner,
                          BigDecimal value,
                          LocalDateTime timestamp) {
}
