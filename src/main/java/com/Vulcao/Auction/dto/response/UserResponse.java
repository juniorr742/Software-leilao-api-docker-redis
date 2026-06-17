package com.Vulcao.Auction.dto.response;

import java.util.List;
import java.util.UUID;

public record UserResponse(UUID idUser,
                           String name,
                           List<ProductResponse> products,
                           List<BidResponse> bids) {
}
