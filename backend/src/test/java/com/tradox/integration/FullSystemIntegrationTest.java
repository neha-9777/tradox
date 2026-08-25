package com.tradox.integration;

import com.tradox.dto.request.*;
import com.tradox.dto.response.*;
import com.tradox.entity.BrokerSyncLog;
import com.tradox.entity.Transaction.TransactionType;
import com.tradox.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class FullSystemIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BrokerSyncService brokerSyncService;

    @Autowired
    private PnLEngineService pnlEngineService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private InsightEngineService insightEngineService;

    @Autowired
    private AuditLogService auditLogService;

    @Test
    @DisplayName("End-to-End System Test: Registration -> Portfolio -> Trade -> Broker Sync -> PnL -> Analytics -> Insights -> Audit Trail")
    void testFullSystemWorkflow() {
        // 1. User Registration
        String email = "e2e_trader_" + System.currentTimeMillis() + "@tradox-test.com";
        RegisterRequest regReq = RegisterRequest.builder()
                .email(email)
                .password("SecurePass123!")
                .firstName("Alex")
                .lastName("Trader")
                .build();

        AuthResponse authResp = authService.register(regReq);
        assertNotNull(authResp);
        assertEquals(email, authResp.getEmail());

        // 2. Portfolio Retrieval
        List<PortfolioResponse> portfolios = portfolioService.getUserPortfolios(email);
        assertFalse(portfolios.isEmpty());
        Long portfolioId = portfolios.get(0).getId();

        // 3. Trade Execution (BUY RELIANCE)
        TransactionRequest buyReq = TransactionRequest.builder()
                .symbol("RELIANCE")
                .assetName("Reliance Industries Ltd")
                .exchange("NSE")
                .type(TransactionType.BUY)
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("2500.00"))
                .fee(new BigDecimal("20.00"))
                .transactionDate(LocalDateTime.now().minusDays(10))
                .build();

        TransactionResponse txResp = transactionService.addTransaction(email, portfolioId, buyReq);
        assertNotNull(txResp);
        assertEquals("RELIANCE", txResp.getAsset().getSymbol());

        // 4. Broker Connection & Data Sync
        ConnectBrokerRequest brokerReq = ConnectBrokerRequest.builder()
                .brokerId("MOCK_BROKER")
                .accountName("My Mock Account")
                .apiKey("test-key")
                .build();

        BrokerConnectionResponse connResp = brokerSyncService.connectBroker(email, brokerReq);
        assertNotNull(connResp);

        BrokerSyncLogResponse syncResp = brokerSyncService.syncBrokerData(email, connResp.getId());
        assertNotNull(syncResp);
        assertEquals(BrokerSyncLog.SyncStatus.SUCCESS, syncResp.getStatus());

        // 5. P&L Engine Calculation
        PnLResponse pnl = pnlEngineService.getUserPnLSummary(email);
        assertNotNull(pnl);
        assertNotNull(pnl.getTotalUnrealizedPnL());

        // 6. Analytics Dashboard
        AnalyticsDashboardResponse analytics = analyticsService.getAnalyticsDashboard(email);
        assertNotNull(analytics);
        assertNotNull(analytics.getSectorAllocation());

        // 7. Intelligent Insights Dashboard
        InsightDashboardResponse insights = insightEngineService.generatePortfolioInsights(email);
        assertNotNull(insights);
        assertTrue(insights.getOverallHealthScore() >= 0 && insights.getOverallHealthScore() <= 100);

        // 8. Audit Trail Verification
        auditLogService.logEvent(email, "SYSTEM_TEST", "Integration", "SUCCESS", "E2E integration test executed");
        List<AuditLogResponse> auditLogs = auditLogService.getUserAuditLogs(email);
        assertNotNull(auditLogs);
        assertFalse(auditLogs.isEmpty());
    }
}
