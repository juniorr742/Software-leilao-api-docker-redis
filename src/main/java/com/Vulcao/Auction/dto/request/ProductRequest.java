package com.Vulcao.Auction.dto.request;



import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(String name,
                             String description,
                             BigDecimal price,
                             UUID idUser) {
}
