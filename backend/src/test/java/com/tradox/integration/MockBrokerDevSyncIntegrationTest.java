package com.tradox.integration;

import com.tradox.dto.request.RegisterRequest;
import com.tradox.dto.response.BrokerSyncLogResponse;
import com.tradox.dto.response.PortfolioResponse;
import com.tradox.dto.response.TransactionResponse;
import com.tradox.entity.BrokerSyncLog;
import com.tradox.service.AuthService;
import com.tradox.service.BrokerSyncService;
import com.tradox.service.PortfolioService;
import com.tradox.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class MockBrokerDevSyncIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private BrokerSyncService brokerSyncService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private TransactionService transactionService;

    @Test
    @DisplayName("Dev Mock Synchronization: Auto-connect, Deduplication, Holdings & PnL Recalculation")
    void testDevMockSyncWorkflow() {
        // 1. Register a test user
        String email = "dev_sync_user_" + System.currentTimeMillis() + "@tradox-dev.com";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(email)
                .password("Password123!")
                .firstName("Dev")
                .lastName("SyncTester")
                .build();
        authService.register(registerRequest);

        // 2. Trigger Dev Mock Sync first time
        BrokerSyncLogResponse firstSync = brokerSyncService.triggerDevMockSync(email);

        assertNotNull(firstSync);
        assertEquals(BrokerSyncLog.SyncStatus.SUCCESS, firstSync.getStatus());
        assertEquals(5, firstSync.getHoldingsSynced()); // 4 holdings + 1 position
        assertEquals(7, firstSync.getTransactionsSynced()); // 7 mock transactions

        // 3. Trigger Dev Mock Sync second time to test deduplication
        BrokerSyncLogResponse secondSync = brokerSyncService.triggerDevMockSync(email);

        assertNotNull(secondSync);
        assertEquals(BrokerSyncLog.SyncStatus.SUCCESS, secondSync.getStatus());
        assertEquals(5, secondSync.getHoldingsSynced());
        assertEquals(0, secondSync.getTransactionsSynced()); // 0 new transactions synced due to deduplication!

        // 4. Verify User Portfolio state after sync
        List<PortfolioResponse> portfolios = portfolioService.getUserPortfolios(email);
        assertFalse(portfolios.isEmpty());
        PortfolioResponse defaultPortfolio = portfolios.get(0);

        assertTrue(defaultPortfolio.getTotalHoldingsCount() > 0);
        assertTrue(defaultPortfolio.getTotalInvested().compareTo(BigDecimal.ZERO) > 0);

        // 5. Verify transactions and PnL details
        List<TransactionResponse> allTx = transactionService.getAllUserTransactions(email);
        assertEquals(7, allTx.size());

        // Check TCS profit sell transaction (realized profit)
        TransactionResponse tcsSellTx = allTx.stream()
                .filter(t -> "TCS".equals(t.getAsset().getSymbol()) && "SELL".equals(t.getType().name()))
                .findFirst()
                .orElse(null);
        assertNotNull(tcsSellTx);
        assertNotNull(tcsSellTx.getRealizedPnL());
        assertTrue(tcsSellTx.getRealizedPnL().compareTo(BigDecimal.ZERO) > 0, "TCS sell should have positive realized PnL");

        // Check TATASTEEL loss sell transaction (realized loss)
        TransactionResponse steelSellTx = allTx.stream()
                .filter(t -> "TATASTEEL".equals(t.getAsset().getSymbol()) && "SELL".equals(t.getType().name()))
                .findFirst()
                .orElse(null);
        assertNotNull(steelSellTx);
        assertNotNull(steelSellTx.getRealizedPnL());
        assertTrue(steelSellTx.getRealizedPnL().compareTo(BigDecimal.ZERO) < 0, "TATASTEEL sell should have negative realized PnL");
    }
}
