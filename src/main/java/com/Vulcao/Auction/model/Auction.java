package com.Vulcao.Auction.model;

import com.Vulcao.Auction.model.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "auction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idAuction;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_idProduct", nullable = false)
    private Product product;

    @Column(nullable = false)
    private BigDecimal actualPrice;

    private LocalDateTime startAction;

    private LocalDateTime finishAction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bid> bids = new ArrayList<>();
}
