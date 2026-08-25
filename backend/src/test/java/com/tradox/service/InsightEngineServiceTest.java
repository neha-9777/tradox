package com.tradox.service;

import com.tradox.dto.response.*;
import com.tradox.dto.response.InsightItemResponse.InsightCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsightEngineServiceTest {

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private PnLEngineService pnlEngineService;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private InsightEngineService insightEngineService;

    private AnalyticsDashboardResponse sampleAnalytics;

    @BeforeEach
    void setUp() {
        PortfolioSummaryResponse summary = PortfolioSummaryResponse.builder()
                .totalInvestment(new BigDecimal("100000.00"))
                .currentValue(new BigDecimal("115000.00"))
                .totalPnL(new BigDecimal("15000.00"))
                .returnPercentage(new BigDecimal("15.00"))
                .activeHoldingsCount(4)
                .totalPortfoliosCount(1)
                .build();

        List<AllocationPointResponse> assetAlloc = List.of(
                AllocationPointResponse.builder().label("EQUITY").percentage(new BigDecimal("80.0")).build(),
                AllocationPointResponse.builder().label("ETF").percentage(new BigDecimal("20.0")).build()
        );

        List<AllocationPointResponse> sectorAlloc = List.of(
                AllocationPointResponse.builder().label("Energy").percentage(new BigDecimal("40.0")).build(),
                AllocationPointResponse.builder().label("Information Technology").percentage(new BigDecimal("35.0")).build(),
                AllocationPointResponse.builder().label("Banking").percentage(new BigDecimal("25.0")).build()
        );

        List<ConcentrationAlertResponse> concentrationAlerts = List.of(
                ConcentrationAlertResponse.builder()
                        .symbol("RELIANCE")
                        .percentage(new BigDecimal("40.0"))
                        .riskLevel("HIGH")
                        .message("RELIANCE represents 40.0% of your total portfolio value.")
                        .build()
        );

        List<AssetPnLResponse> bestPerformers = List.of(
                AssetPnLResponse.builder()
                        .symbol("RELIANCE")
                        .returnPercentage(new BigDecimal("20.0"))
                        .totalPnL(new BigDecimal("10000.00"))
                        .build()
        );

        sampleAnalytics = AnalyticsDashboardResponse.builder()
                .portfolioSummary(summary)
                .assetAllocation(assetAlloc)
                .sectorAllocation(sectorAlloc)
                .bestPerformers(bestPerformers)
                .concentrationAlerts(concentrationAlerts)
                .build();
    }

    @Test
    @DisplayName("Should deduct health score when high concentration alert exists")
    void testCalculateHealthScore_HighConcentration() {
        int score = insightEngineService.calculateHealthScore(sampleAnalytics);

        // Base 85 - 15 (high concentration) + 10 (3+ sectors) = 80
        assertEquals(80, score);
    }

    @Test
    @DisplayName("Should evaluate risk rating to AGGRESSIVE when high concentration alert is present")
    void testEvaluateRiskRating_Aggressive() {
        String rating = insightEngineService.evaluateRiskRating(sampleAnalytics);

        assertEquals("AGGRESSIVE", rating);
    }

    @Test
    @DisplayName("Should generate complete portfolio insights payload")
    void testGeneratePortfolioInsights_Full() {
        when(analyticsService.getAnalyticsDashboard(anyString())).thenReturn(sampleAnalytics);

        InsightDashboardResponse dashboard = insightEngineService.generatePortfolioInsights("trader@tradox.com");

        assertNotNull(dashboard);
        assertNotNull(dashboard.getOverallHealthScore());
        assertEquals("AGGRESSIVE", dashboard.getRiskRating());
        assertNotNull(dashboard.getExecutiveSummary());

        List<InsightItemResponse> insights = dashboard.getInsights();
        assertFalse(insights.isEmpty());

        boolean hasSummary = insights.stream().anyMatch(i -> i.getCategory() == InsightCategory.SUMMARY);
        boolean hasConcentration = insights.stream().anyMatch(i -> i.getCategory() == InsightCategory.CONCENTRATION);
        boolean hasPerformance = insights.stream().anyMatch(i -> i.getCategory() == InsightCategory.PERFORMANCE);

        assertTrue(hasSummary);
        assertTrue(hasConcentration);
        assertTrue(hasPerformance);
    }
}
