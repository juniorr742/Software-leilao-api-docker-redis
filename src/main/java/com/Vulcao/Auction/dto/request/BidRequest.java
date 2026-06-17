package com.Vulcao.Auction.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BidRequest(UUID idUser,
                         BigDecimal value,
                         LocalDateTime timestamp) {
}
