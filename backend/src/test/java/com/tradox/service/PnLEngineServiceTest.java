package com.tradox.service;

import com.tradox.dto.response.PnLResponse;
import com.tradox.entity.Asset;
import com.tradox.entity.Holding;
import com.tradox.entity.Portfolio;
import com.tradox.entity.Transaction;
import com.tradox.entity.Transaction.TransactionType;
import com.tradox.entity.User;
import com.tradox.repository.HoldingRepository;
import com.tradox.repository.PortfolioRepository;
import com.tradox.repository.TransactionRepository;
import com.tradox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PnLEngineServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PnLEngineService pnlEngineService;

    private User sampleUser;
    private Portfolio samplePortfolio;
    private Asset sampleAsset;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .email("trader@tradox.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        samplePortfolio = Portfolio.builder()
                .id(10L)
                .user(sampleUser)
                .name("Test Portfolio")
                .currency("INR")
                .isDefault(true)
                .build();

        sampleAsset = Asset.builder()
                .id(100L)
                .symbol("RELIANCE")
                .name("Reliance Industries Ltd")
                .exchange("NSE")
                .currentPrice(new BigDecimal("2800.00"))
                .build();
    }

    @Test
    @DisplayName("Should correctly calculate Realized P&L on a profitable SELL order")
    void testCalculateRealizedPnL_Profit() {
        BigDecimal sellQty = new BigDecimal("10");
        BigDecimal sellPrice = new BigDecimal("2800.00");
        BigDecimal avgCostPrice = new BigDecimal("2500.00");
        BigDecimal fee = new BigDecimal("20.00");

        // Expected: (2800 - 2500) * 10 - 20 = 3000 - 20 = 2980
        BigDecimal realizedPnL = pnlEngineService.calculateRealizedPnL(sellQty, sellPrice, avgCostPrice, fee);

        assertEquals(0, new BigDecimal("2980.00").compareTo(realizedPnL));
    }

    @Test
    @DisplayName("Should correctly calculate Realized P&L on a loss SELL order with fees")
    void testCalculateRealizedPnL_Loss() {
        BigDecimal sellQty = new BigDecimal("5");
        BigDecimal sellPrice = new BigDecimal("2200.00");
        BigDecimal avgCostPrice = new BigDecimal("2500.00");
        BigDecimal fee = new BigDecimal("15.00");

        // Expected: (2200 - 2500) * 5 - 15 = -1500 - 15 = -1515
        BigDecimal realizedPnL = pnlEngineService.calculateRealizedPnL(sellQty, sellPrice, avgCostPrice, fee);

        assertEquals(0, new BigDecimal("-1515.00").compareTo(realizedPnL));
    }

    @Test
    @DisplayName("Should correctly calculate Unrealized P&L on active position")
    void testCalculateUnrealizedPnL_Gain() {
        BigDecimal quantity = new BigDecimal("20");
        BigDecimal avgBuyPrice = new BigDecimal("2500.00");
        BigDecimal currentPrice = new BigDecimal("2850.00");

        // Expected: (2850 - 2500) * 20 = 7000
        BigDecimal unrealizedPnL = pnlEngineService.calculateUnrealizedPnL(quantity, avgBuyPrice, currentPrice);

        assertEquals(0, new BigDecimal("7000.00").compareTo(unrealizedPnL));
    }

    @Test
    @DisplayName("Should calculate Return Percentage correctly")
    void testCalculateReturnPercentage_Positive() {
        BigDecimal totalInvested = new BigDecimal("50000.00");
        BigDecimal currentValue = new BigDecimal("57000.00");
        BigDecimal totalRealizedPnL = new BigDecimal("3000.00");

        // Expected: ((57000 + 3000 - 50000) / 50000) * 100 = (10000 / 50000) * 100 = 20%
        BigDecimal returnPercentage = pnlEngineService.calculateReturnPercentage(totalInvested, currentValue, totalRealizedPnL);

        assertEquals(0, new BigDecimal("20.0000").compareTo(returnPercentage));
    }

    @Test
    @DisplayName("Should return zero Return Percentage when zero total investment")
    void testCalculateReturnPercentage_ZeroInvestment() {
        BigDecimal returnPercentage = pnlEngineService.calculateReturnPercentage(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, returnPercentage);
    }

    @Test
    @DisplayName("Should aggregate user P&L summary correctly")
    void testGetUserPnLSummary() {
        Transaction sellTx = Transaction.builder()
                .id(50L)
                .portfolio(samplePortfolio)
                .asset(sampleAsset)
                .type(TransactionType.SELL)
                .quantity(new BigDecimal("5"))
                .price(new BigDecimal("2900.00"))
                .totalAmount(new BigDecimal("14500.00"))
                .realizedPnL(new BigDecimal("2000.00"))
                .transactionDate(LocalDateTime.now())
                .build();

        Holding holding = Holding.builder()
                .id(30L)
                .portfolio(samplePortfolio)
                .asset(sampleAsset)
                .quantity(new BigDecimal("15"))
                .averageBuyPrice(new BigDecimal("2500.00"))
                .totalInvested(new BigDecimal("37500.00"))
                .currentPrice(new BigDecimal("2800.00"))
                .currentValue(new BigDecimal("42000.00"))
                .unrealizedPnL(new BigDecimal("4500.00"))
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));
        when(portfolioRepository.findByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(List.of(samplePortfolio));
        when(transactionRepository.findByPortfolioUserIdOrderByTransactionDateDesc(anyLong())).thenReturn(List.of(sellTx));
        when(holdingRepository.findByPortfolioIdOrderByTotalInvestedDesc(anyLong())).thenReturn(List.of(holding));

        PnLResponse response = pnlEngineService.getUserPnLSummary("trader@tradox.com");

        assertNotNull(response);
        assertEquals(0, new BigDecimal("2000.00").compareTo(response.getTotalRealizedPnL()));
        assertEquals(0, new BigDecimal("4500.00").compareTo(response.getTotalUnrealizedPnL()));
        assertEquals(0, new BigDecimal("6500.00").compareTo(response.getOverallPnL()));
        assertEquals(1, response.getAssetPnLBreakdown().size());
        assertEquals("RELIANCE", response.getAssetPnLBreakdown().get(0).getSymbol());
    }
}
