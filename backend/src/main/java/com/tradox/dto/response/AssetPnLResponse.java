package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetPnLResponse {

    private String symbol;
    private String name;
    private String exchange;
    private BigDecimal quantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal currentPrice;
    private BigDecimal realizedPnL;
    private BigDecimal unrealizedPnL;
    private BigDecimal totalPnL;
    private BigDecimal returnPercentage;
}
