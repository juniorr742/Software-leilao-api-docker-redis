package com.Vulcao.Auction.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UserRequest(String name,
                          String email,
                          String password,
                          BigDecimal saldo,
                          List<UUID> listProducts,
                          List<UUID> listBids) {
}
