package com.tradox.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"symbol", "exchange"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String symbol;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false, length = 30)
    @Builder.Default
    private AssetType assetType = AssetType.EQUITY;

    @Column(length = 20)
    @Builder.Default
    private String exchange = "NSE";

    @Column(length = 100)
    private String sector;

    @Column(length = 10)
    @Builder.Default
    private String currency = "INR";

    @Column(name = "current_price", precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum AssetType {
        EQUITY, ETF, MUTUAL_FUND, CRYPTO
    }
}
