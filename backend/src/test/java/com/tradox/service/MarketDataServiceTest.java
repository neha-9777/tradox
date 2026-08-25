package com.tradox.service;

import com.tradox.dto.response.MarketQuoteResponse;
import com.tradox.dto.response.PriceHistoryPointResponse;
import com.tradox.entity.Asset;
import com.tradox.entity.Holding;
import com.tradox.entity.Portfolio;
import com.tradox.entity.User;
import com.tradox.repository.AssetRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketDataServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MarketDataService marketDataService;

    private User sampleUser;
    private Portfolio samplePortfolio;
    private Asset sampleAsset;
    private Holding sampleHolding;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .email("trader@tradox.com")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        samplePortfolio = Portfolio.builder()
                .id(10L)
                .user(sampleUser)
                .name("Growth Portfolio")
                .isDefault(true)
                .build();

        sampleAsset = Asset.builder()
                .id(100L)
                .symbol("TCS")
                .name("Tata Consultancy Services")
                .exchange("NSE")
                .currentPrice(new BigDecimal("3900.00"))
                .build();

        sampleHolding = Holding.builder()
                .id(50L)
                .portfolio(samplePortfolio)
                .asset(sampleAsset)
                .quantity(new BigDecimal("10"))
                .averageBuyPrice(new BigDecimal("3500.00"))
                .totalInvested(new BigDecimal("35000.00"))
                .currentPrice(new BigDecimal("3500.00"))
                .currentValue(new BigDecimal("35000.00"))
                .unrealizedPnL(BigDecimal.ZERO)
                .build();
    }

    @Test
    @DisplayName("Should return fresh market quote with data source and timestamp")
    void testGetMarketQuote_Fresh() {
        when(assetRepository.findBySymbolIgnoreCase("TCS")).thenReturn(Optional.of(sampleAsset));

        MarketQuoteResponse quote = marketDataService.getMarketQuote("TCS");

        assertNotNull(quote);
        assertEquals("TCS", quote.getSymbol());
        assertEquals("NSE", quote.getExchange());
        assertNotNull(quote.getCurrentPrice());
        assertNotNull(quote.getTimestamp());
        assertEquals("PROVIDER_SIMULATED_FEEDS", quote.getDataSource());
    }

    @Test
    @DisplayName("Should return cached quote when called multiple times within TTL")
    void testGetMarketQuote_CacheHit() {
        when(assetRepository.findBySymbolIgnoreCase("INFY")).thenReturn(Optional.empty());

        MarketQuoteResponse firstQuote = marketDataService.getMarketQuote("INFY");
        MarketQuoteResponse secondQuote = marketDataService.getMarketQuote("INFY");

        assertSame(firstQuote, secondQuote);
        assertEquals("INFY", secondQuote.getSymbol());
    }

    @Test
    @DisplayName("Should return historical daily price candles")
    void testGetHistoricalPrices() {
        List<PriceHistoryPointResponse> history = marketDataService.getHistoricalPrices("RELIANCE", 30);

        assertNotNull(history);
        assertEquals(31, history.size()); // 30 days + today
        assertNotNull(history.get(0).getClosePrice());
        assertNotNull(history.get(0).getDate());
    }

    @Test
    @DisplayName("Should refresh holding prices and update unrealized P&L")
    void testRefreshPortfolioHoldings() {
        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(portfolioRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(samplePortfolio));
        when(holdingRepository.findByPortfolioIdOrderByTotalInvestedDesc(10L)).thenReturn(List.of(sampleHolding));
        when(assetRepository.findBySymbolIgnoreCase("TCS")).thenReturn(Optional.of(sampleAsset));
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));
        when(holdingRepository.save(any(Holding.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Holding> updated = marketDataService.refreshPortfolioHoldings("trader@tradox.com", 10L);

        assertNotNull(updated);
        assertEquals(1, updated.size());

        Holding refreshed = updated.get(0);
        assertNotNull(refreshed.getCurrentPrice());
        assertTrue(refreshed.getCurrentValue().compareTo(BigDecimal.ZERO) > 0);
    }
}
