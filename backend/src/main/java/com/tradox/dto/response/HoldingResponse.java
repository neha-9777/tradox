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
public class HoldingResponse {

    private Long id;
    private Long portfolioId;
    private AssetResponse asset;
    private BigDecimal quantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal totalInvested;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;
    private BigDecimal unrealizedPnL;
    private BigDecimal unrealizedPnLPercentage;
}
