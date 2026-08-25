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
public class PortfolioSummaryResponse {

    private BigDecimal totalInvestment;
    private BigDecimal currentValue;
    private BigDecimal totalPnL;
    private BigDecimal returnPercentage;
    private Integer activeHoldingsCount;
    private Integer totalPortfoliosCount;
}
