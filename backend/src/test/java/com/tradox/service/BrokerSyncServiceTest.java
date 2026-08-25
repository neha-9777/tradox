package com.tradox.service;

import com.tradox.broker.impl.MockBrokerConnector;
import com.tradox.dto.request.ConnectBrokerRequest;
import com.tradox.dto.response.BrokerConnectionResponse;
import com.tradox.dto.response.BrokerSyncLogResponse;
import com.tradox.entity.*;
import com.tradox.entity.BrokerConnection.ConnectionStatus;
import com.tradox.entity.BrokerSyncLog.SyncStatus;
import com.tradox.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerSyncServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BrokerConnectionRepository connectionRepository;

    @Mock
    private BrokerSyncLogRepository syncLogRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AssetService assetService;

    private BrokerSyncService brokerSyncService;
    private MockBrokerConnector mockBrokerConnector;

    private User sampleUser;
    private Portfolio samplePortfolio;
    private BrokerConnection sampleConnection;

    @BeforeEach
    void setUp() {
        mockBrokerConnector = new MockBrokerConnector();
        brokerSyncService = new BrokerSyncService(
                List.of(mockBrokerConnector),
                userRepository,
                connectionRepository,
                syncLogRepository,
                portfolioRepository,
                portfolioService,
                transactionService,
                assetService
        );

        sampleUser = User.builder()
                .id(1L)
                .email("trader@tradox.com")
                .firstName("Sam")
                .lastName("Wilson")
                .build();

        samplePortfolio = Portfolio.builder()
                .id(10L)
                .user(sampleUser)
                .name("Main Portfolio")
                .isDefault(true)
                .build();

        sampleConnection = BrokerConnection.builder()
                .id(50L)
                .user(sampleUser)
                .brokerId("MOCK_BROKER")
                .accountName("My Mock Account")
                .status(ConnectionStatus.CONNECTED)
                .accessToken("token-123")
                .build();
    }

    @Test
    @DisplayName("Should successfully connect a new broker account")
    void testConnectBroker() {
        ConnectBrokerRequest req = ConnectBrokerRequest.builder()
                .brokerId("MOCK_BROKER")
                .accountName("My Mock Account")
                .apiKey("api-key-test")
                .build();

        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(connectionRepository.findByUserIdAndBrokerId(1L, "MOCK_BROKER")).thenReturn(Optional.empty());
        when(connectionRepository.save(any(BrokerConnection.class))).thenAnswer(inv -> {
            BrokerConnection conn = inv.getArgument(0);
            conn.setId(50L);
            return conn;
        });

        BrokerConnectionResponse resp = brokerSyncService.connectBroker("trader@tradox.com", req);

        assertNotNull(resp);
        assertEquals("MOCK_BROKER", resp.getBrokerId());
        assertEquals("My Mock Account", resp.getAccountName());
        assertEquals(ConnectionStatus.CONNECTED, resp.getStatus());
    }

    @Test
    @DisplayName("Should sync broker holdings and transactions successfully")
    void testSyncBrokerData_Success() {
        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(connectionRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(sampleConnection));
        when(portfolioRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(samplePortfolio));

        when(syncLogRepository.save(any(BrokerSyncLog.class))).thenAnswer(inv -> {
            BrokerSyncLog log = inv.getArgument(0);
            if (log.getId() == null) log.setId(100L);
            return log;
        });

        when(transactionService.existsByPortfolioAndExternalTxId(eq(10L), anyString())).thenReturn(false);

        BrokerSyncLogResponse logResp = brokerSyncService.syncBrokerData("trader@tradox.com", 50L);

        assertNotNull(logResp);
        assertEquals(SyncStatus.SUCCESS, logResp.getStatus());
        assertTrue(logResp.getHoldingsSynced() > 0);
        assertTrue(logResp.getTransactionsSynced() > 0);
        verify(transactionService, times(logResp.getTransactionsSynced())).addTransaction(eq("trader@tradox.com"), eq(10L), any());
    }

    @Test
    @DisplayName("Should skip duplicate transactions during synchronization")
    void testSyncBrokerData_Deduplication() {
        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(connectionRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(sampleConnection));
        when(portfolioRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(samplePortfolio));

        when(syncLogRepository.save(any(BrokerSyncLog.class))).thenAnswer(inv -> {
            BrokerSyncLog log = inv.getArgument(0);
            if (log.getId() == null) log.setId(100L);
            return log;
        });

        // Simulate all transactions already synced
        when(transactionService.existsByPortfolioAndExternalTxId(eq(10L), anyString())).thenReturn(true);

        BrokerSyncLogResponse logResp = brokerSyncService.syncBrokerData("trader@tradox.com", 50L);

        assertNotNull(logResp);
        assertEquals(SyncStatus.SUCCESS, logResp.getStatus());
        assertEquals(0, logResp.getTransactionsSynced());
        verify(transactionService, never()).addTransaction(anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("Should trigger dev mock sync and auto-connect if connection missing")
    void testTriggerDevMockSync() {
        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(connectionRepository.findByUserIdAndBrokerId(1L, "MOCK_BROKER")).thenReturn(Optional.empty());
        when(connectionRepository.save(any(BrokerConnection.class))).thenAnswer(inv -> {
            BrokerConnection conn = inv.getArgument(0);
            if (conn.getId() == null) conn.setId(50L);
            return conn;
        });
        when(connectionRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(sampleConnection));
        when(portfolioRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(samplePortfolio));
        when(syncLogRepository.save(any(BrokerSyncLog.class))).thenAnswer(inv -> {
            BrokerSyncLog log = inv.getArgument(0);
            if (log.getId() == null) log.setId(100L);
            return log;
        });

        BrokerSyncLogResponse logResp = brokerSyncService.triggerDevMockSync("trader@tradox.com");

        assertNotNull(logResp);
        assertEquals(SyncStatus.SUCCESS, logResp.getStatus());
    }

    @Test
    @DisplayName("Should disconnect a broker connection")
    void testDisconnectBroker() {
        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(connectionRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(sampleConnection));

        brokerSyncService.disconnectBroker("trader@tradox.com", 50L);

        assertEquals(ConnectionStatus.DISCONNECTED, sampleConnection.getStatus());
    }
}
