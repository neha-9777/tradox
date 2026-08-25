package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDashboardResponse {

    private PortfolioSummaryResponse portfolioSummary;
    private List<AllocationPointResponse> assetAllocation;
    private List<AllocationPointResponse> sectorAllocation;
    private List<AssetPnLResponse> bestPerformers;
    private List<AssetPnLResponse> worstPerformers;
    private List<ConcentrationAlertResponse> concentrationAlerts;
    private List<PriceHistoryPointResponse> historicalPerformance;
}
