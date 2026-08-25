package com.tradox.service;

import com.tradox.dto.response.*;
import com.tradox.dto.response.InsightItemResponse.InsightCategory;
import com.tradox.dto.response.InsightItemResponse.InsightSeverity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InsightEngineService {

    private final AnalyticsService analyticsService;
    private final PnLEngineService pnlEngineService;
    private final PortfolioService portfolioService;

    public InsightEngineService(AnalyticsService analyticsService,
                                PnLEngineService pnlEngineService,
                                PortfolioService portfolioService) {
        this.analyticsService = analyticsService;
        this.pnlEngineService = pnlEngineService;
        this.portfolioService = portfolioService;
    }

    public InsightDashboardResponse generatePortfolioInsights(String userEmail) {
        AnalyticsDashboardResponse analytics = analyticsService.getAnalyticsDashboard(userEmail);
        PortfolioSummaryResponse summary = analytics.getPortfolioSummary();

        List<InsightItemResponse> insights = new ArrayList<>();

        int healthScore = calculateHealthScore(analytics);
        String riskRating = evaluateRiskRating(analytics);

        // 1. Executive Summary Insight
        insights.add(InsightItemResponse.builder()
                .id(UUID.randomUUID().toString())
                .category(InsightCategory.SUMMARY)
                .severity(InsightSeverity.INFO)
                .title("Portfolio Health Synthesis")
                .description(String.format("Your portfolio is currently evaluated at a %d/100 health score with a %s risk profile.", healthScore, riskRating))
                .actionableAdvice("Maintain periodic quarterly rebalancing to keep asset allocations aligned with your risk tolerance.")
                .score(healthScore)
                .build());

        // 2. Concentration Warnings
        List<ConcentrationAlertResponse> concentrationAlerts = analytics.getConcentrationAlerts();
        if (concentrationAlerts != null && !concentrationAlerts.isEmpty()) {
            for (ConcentrationAlertResponse alert : concentrationAlerts) {
                InsightSeverity sev = "HIGH".equals(alert.getRiskLevel()) ? InsightSeverity.DANGER : InsightSeverity.WARNING;
                insights.add(InsightItemResponse.builder()
                        .id(UUID.randomUUID().toString())
                        .category(InsightCategory.CONCENTRATION)
                        .severity(sev)
                        .title("Overexposure Warning: " + alert.getSymbol())
                        .description(alert.getMessage())
                        .actionableAdvice("Consider trimming " + alert.getSymbol() + " and allocating profits into index ETFs or underrepresented sectors.")
                        .symbol(alert.getSymbol())
                        .build());
            }
        } else {
            insights.add(InsightItemResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .category(InsightCategory.CONCENTRATION)
                    .severity(InsightSeverity.SUCCESS)
                    .title("Healthy Position Diversification")
                    .description("No individual stock position exceeds the 25% single-asset risk threshold.")
                    .actionableAdvice("Great job maintaining single-stock position limits.")
                    .build());
        }

        // 3. Performance & Profit-Taking Observations
        List<AssetPnLResponse> bestPerformers = analytics.getBestPerformers();
        if (bestPerformers != null && !bestPerformers.isEmpty()) {
            AssetPnLResponse top = bestPerformers.get(0);
            if (top.getReturnPercentage() != null && top.getReturnPercentage().compareTo(new BigDecimal("15.0")) >= 0) {
                insights.add(InsightItemResponse.builder()
                        .id(UUID.randomUUID().toString())
                        .category(InsightCategory.PERFORMANCE)
                        .severity(InsightSeverity.SUCCESS)
                        .title("Strong Outperformer: " + top.getSymbol())
                        .description(String.format("%s has generated a stellar +%.2f%% return (+₹%.2f PnL).",
                                top.getSymbol(), top.getReturnPercentage().doubleValue(), top.getTotalPnL().doubleValue()))
                        .actionableAdvice("Evaluate setting a trailing stop-loss or locking in partial profits.")
                        .symbol(top.getSymbol())
                        .build());
            }
        }

        // 4. Sector Trend & Accumulation Detection
        List<AllocationPointResponse> sectors = analytics.getSectorAllocation();
        if (sectors != null && !sectors.isEmpty()) {
            AllocationPointResponse topSector = sectors.get(0);
            insights.add(InsightItemResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .category(InsightCategory.TREND)
                    .severity(InsightSeverity.INFO)
                    .title("Primary Sector Concentration: " + topSector.getLabel())
                    .description(String.format("%s dominates your holdings at %.1f%% of total capital.", topSector.getLabel(), topSector.getPercentage().doubleValue()))
                    .actionableAdvice("Ensure you are comfortable with " + topSector.getLabel() + " sector exposure during economic cycles.")
                    .build());
        }

        String execSummary = String.format("Portfolio Health Score is %d/100 (%s Risk Profile). %d actionable insight(s) generated.",
                healthScore, riskRating, insights.size());

        return InsightDashboardResponse.builder()
                .overallHealthScore(healthScore)
                .riskRating(riskRating)
                .executiveSummary(execSummary)
                .insights(insights)
                .build();
    }

    public int calculateHealthScore(AnalyticsDashboardResponse analytics) {
        int score = 85;

        List<ConcentrationAlertResponse> alerts = analytics.getConcentrationAlerts();
        if (alerts != null) {
            for (ConcentrationAlertResponse alert : alerts) {
                if ("HIGH".equals(alert.getRiskLevel())) {
                    score -= 15;
                } else if ("MEDIUM".equals(alert.getRiskLevel())) {
                    score -= 10;
                }
            }
        }

        List<AllocationPointResponse> sectors = analytics.getSectorAllocation();
        if (sectors != null && sectors.size() >= 3) {
            score += 10;
        }

        PortfolioSummaryResponse summary = analytics.getPortfolioSummary();
        if (summary != null && summary.getReturnPercentage() != null && summary.getReturnPercentage().compareTo(BigDecimal.ZERO) < 0) {
            score -= 10;
        }

        return Math.max(0, Math.min(100, score));
    }

    public String evaluateRiskRating(AnalyticsDashboardResponse analytics) {
        List<ConcentrationAlertResponse> alerts = analytics.getConcentrationAlerts();
        if (alerts != null) {
            for (ConcentrationAlertResponse alert : alerts) {
                if ("HIGH".equals(alert.getRiskLevel())) {
                    return "AGGRESSIVE";
                }
            }
        }

        List<AllocationPointResponse> assetAlloc = analytics.getAssetAllocation();
        if (assetAlloc != null) {
            for (AllocationPointResponse alloc : assetAlloc) {
                if ("ETF".equalsIgnoreCase(alloc.getLabel()) || "MUTUAL_FUND".equalsIgnoreCase(alloc.getLabel())) {
                    if (alloc.getPercentage() != null && alloc.getPercentage().compareTo(new BigDecimal("40.0")) >= 0) {
                        return "CONSERVATIVE";
                    }
                }
            }
        }

        return "MODERATE";
    }
}
