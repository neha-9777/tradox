package com.tradox.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "holdings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"portfolio_id", "asset_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "average_buy_price", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal averageBuyPrice = BigDecimal.ZERO;

    @Column(name = "total_invested", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal totalInvested = BigDecimal.ZERO;

    @Column(name = "current_price", precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @Column(name = "current_value", precision = 19, scale = 4)
    private BigDecimal currentValue;

    @Column(name = "unrealized_pnl", precision = 19, scale = 4)
    private BigDecimal unrealizedPnL;

    @Column(name = "unrealized_pnl_percentage", precision = 10, scale = 4)
    private BigDecimal unrealizedPnLPercentage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
