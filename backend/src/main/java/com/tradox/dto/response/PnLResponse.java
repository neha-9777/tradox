package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PnLResponse {

    private BigDecimal totalRealizedPnL;
    private BigDecimal totalUnrealizedPnL;
    private BigDecimal dailyPnL;
    private BigDecimal weeklyPnL;
    private BigDecimal monthlyPnL;
    private BigDecimal overallPnL;
    private BigDecimal overallReturnPercentage;
    private BigDecimal totalInvestment;
    private BigDecimal currentValue;
    private List<AssetPnLResponse> assetPnLBreakdown;
}
