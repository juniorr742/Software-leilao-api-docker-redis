package com.Vulcao.Auction.dto.response;

import com.Vulcao.Auction.model.User;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID idProduct,
                              User owner,
                              String nameProduct,
                              BigDecimal StartingPrice
                              ) {
}
