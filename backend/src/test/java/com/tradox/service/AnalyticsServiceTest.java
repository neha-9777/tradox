package com.tradox.service;

import com.tradox.dto.response.*;
import com.tradox.entity.Asset;
import com.tradox.entity.Asset.AssetType;
import com.tradox.entity.Holding;
import com.tradox.entity.Portfolio;
import com.tradox.entity.User;
import com.tradox.repository.HoldingRepository;
import com.tradox.repository.PortfolioRepository;
import com.tradox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private PnLEngineService pnlEngineService;

    @Mock
    private MarketDataService marketDataService;

    @InjectMocks
    private AnalyticsService analyticsService;

    private User sampleUser;
    private Portfolio samplePortfolio;
    private Holding holding1;
    private Holding holding2;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .email("trader@tradox.com")
                .firstName("Alice")
                .lastName("Smith")
                .build();

        samplePortfolio = Portfolio.builder()
                .id(10L)
                .user(sampleUser)
                .name("Core Portfolio")
                .isDefault(true)
                .build();

        Asset asset1 = Asset.builder()
                .id(100L)
                .symbol("RELIANCE")
                .name("Reliance Industries")
                .assetType(AssetType.EQUITY)
                .sector("Energy")
                .build();

        Asset asset2 = Asset.builder()
                .id(101L)
                .symbol("TCS")
                .name("Tata Consultancy Services")
                .assetType(AssetType.EQUITY)
                .sector("Information Technology")
                .build();

        // RELIANCE holding = 40,000 (80% of 50,000 total) -> HIGH concentration risk
        holding1 = Holding.builder()
                .id(10L)
                .portfolio(samplePortfolio)
                .asset(asset1)
                .quantity(new BigDecimal("10"))
                .averageBuyPrice(new BigDecimal("4000.00"))
                .totalInvested(new BigDecimal("40000.00"))
                .currentPrice(new BigDecimal("4000.00"))
                .currentValue(new BigDecimal("40000.00"))
                .build();

        // TCS holding = 10,000 (20% of 50,000 total)
        holding2 = Holding.builder()
                .id(11L)
                .portfolio(samplePortfolio)
                .asset(asset2)
                .quantity(new BigDecimal("5"))
                .averageBuyPrice(new BigDecimal("2000.00"))
                .totalInvested(new BigDecimal("10000.00"))
                .currentPrice(new BigDecimal("2000.00"))
                .currentValue(new BigDecimal("10000.00"))
                .build();
    }

    @Test
    @DisplayName("Should correctly calculate asset and sector allocations")
    void testCalculateAllocations() {
        BigDecimal totalVal = new BigDecimal("50000.00");
        List<Holding> holdings = List.of(holding1, holding2);

        List<AllocationPointResponse> assetAlloc = analyticsService.calculateAssetAllocation(holdings, totalVal);
        List<AllocationPointResponse> sectorAlloc = analyticsService.calculateSectorAllocation(holdings, totalVal);

        assertNotNull(assetAlloc);
        assertEquals(1, assetAlloc.size()); // EQUITY
        assertEquals(0, new BigDecimal("100.0000").compareTo(assetAlloc.get(0).getPercentage()));

        assertNotNull(sectorAlloc);
        assertEquals(2, sectorAlloc.size()); // Energy & IT
        assertEquals("Energy", sectorAlloc.get(0).getLabel());
        assertEquals(0, new BigDecimal("80.0000").compareTo(sectorAlloc.get(0).getPercentage()));
    }

    @Test
    @DisplayName("Should flag high concentration risk when holding weight exceeds 35%")
    void testCalculateConcentrationAlerts() {
        BigDecimal totalVal = new BigDecimal("50000.00");
        List<Holding> holdings = List.of(holding1, holding2);

        List<ConcentrationAlertResponse> alerts = analyticsService.calculateConcentrationAlerts(holdings, totalVal);

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals("RELIANCE", alerts.get(0).getSymbol());
        assertEquals("HIGH", alerts.get(0).getRiskLevel());
        assertEquals(0, new BigDecimal("80.0000").compareTo(alerts.get(0).getPercentage()));
    }

    @Test
    @DisplayName("Should assemble complete analytics dashboard payload")
    void testGetAnalyticsDashboard() {
        PortfolioSummaryResponse summary = PortfolioSummaryResponse.builder()
                .totalInvestment(new BigDecimal("50000.00"))
                .currentValue(new BigDecimal("50000.00"))
                .totalPnL(BigDecimal.ZERO)
                .returnPercentage(BigDecimal.ZERO)
                .activeHoldingsCount(2)
                .totalPortfoliosCount(1)
                .build();

        PnLResponse pnlResponse = PnLResponse.builder()
                .totalRealizedPnL(BigDecimal.ZERO)
                .totalUnrealizedPnL(BigDecimal.ZERO)
                .assetPnLBreakdown(List.of(
                        AssetPnLResponse.builder().symbol("RELIANCE").totalPnL(new BigDecimal("1000")).build(),
                        AssetPnLResponse.builder().symbol("TCS").totalPnL(new BigDecimal("-500")).build()
                ))
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));
        when(portfolioService.getOverallSummary(anyString())).thenReturn(summary);
        when(pnlEngineService.getUserPnLSummary(anyString())).thenReturn(pnlResponse);
        when(portfolioRepository.findByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(List.of(samplePortfolio));
        when(holdingRepository.findByPortfolioIdOrderByTotalInvestedDesc(anyLong())).thenReturn(List.of(holding1, holding2));
        when(marketDataService.getHistoricalPrices(anyString(), anyInt())).thenReturn(List.of());

        AnalyticsDashboardResponse response = analyticsService.getAnalyticsDashboard("trader@tradox.com");

        assertNotNull(response);
        assertEquals(2, response.getSectorAllocation().size());
        assertEquals(1, response.getConcentrationAlerts().size());
        assertEquals(2, response.getBestPerformers().size());
    }
}
