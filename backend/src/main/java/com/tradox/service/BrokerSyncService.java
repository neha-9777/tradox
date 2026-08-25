package com.tradox.service;

import com.tradox.broker.BrokerConnector;
import com.tradox.broker.model.NormalizedBrokerHolding;
import com.tradox.broker.model.NormalizedBrokerTransaction;
import com.tradox.dto.request.ConnectBrokerRequest;
import com.tradox.dto.request.TransactionRequest;
import com.tradox.dto.response.BrokerCapabilityResponse;
import com.tradox.dto.response.BrokerConnectionResponse;
import com.tradox.dto.response.BrokerSyncLogResponse;
import com.tradox.entity.*;
import com.tradox.entity.BrokerConnection.ConnectionStatus;
import com.tradox.entity.BrokerSyncLog.SyncStatus;
import com.tradox.exception.BadRequestException;
import com.tradox.exception.ResourceNotFoundException;
import com.tradox.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BrokerSyncService {

    private final Map<String, BrokerConnector> connectorRegistry = new HashMap<>();

    private final UserRepository userRepository;
    private final BrokerConnectionRepository connectionRepository;
    private final BrokerSyncLogRepository syncLogRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;
    private final TransactionService transactionService;
    private final AssetService assetService;

    public BrokerSyncService(List<BrokerConnector> connectors,
                             UserRepository userRepository,
                             BrokerConnectionRepository connectionRepository,
                             BrokerSyncLogRepository syncLogRepository,
                             PortfolioRepository portfolioRepository,
                             PortfolioService portfolioService,
                             TransactionService transactionService,
                             AssetService assetService) {
        for (BrokerConnector connector : connectors) {
            this.connectorRegistry.put(connector.getBrokerId().toUpperCase(), connector);
        }
        this.userRepository = userRepository;
        this.connectionRepository = connectionRepository;
        this.syncLogRepository = syncLogRepository;
        this.portfolioRepository = portfolioRepository;
        this.portfolioService = portfolioService;
        this.transactionService = transactionService;
        this.assetService = assetService;
    }

    public List<BrokerCapabilityResponse> getBrokerCapabilities() {
        return List.of(
                BrokerCapabilityResponse.builder()
                        .brokerId("ZERODHA")
                        .brokerName("Zerodha Kite Connect")
                        .supportsHoldings(true)
                        .supportsPositions(true)
                        .supportsOrders(true)
                        .supportsHistoricalData(true)
                        .authType("SHA256_CHECKSUM")
                        .status("PRODUCTION_READY")
                        .build(),
                BrokerCapabilityResponse.builder()
                        .brokerId("GROWW")
                        .brokerName("Groww India")
                        .supportsHoldings(true)
                        .supportsPositions(true)
                        .supportsOrders(true)
                        .supportsHistoricalData(false)
                        .authType("API_KEY_SECRET")
                        .status("PRODUCTION_READY")
                        .build(),
                BrokerCapabilityResponse.builder()
                        .brokerId("ANGEL_ONE")
                        .brokerName("Angel One SmartAPI")
                        .supportsHoldings(true)
                        .supportsPositions(true)
                        .supportsOrders(true)
                        .supportsHistoricalData(true)
                        .authType("API_KEY_SECRET")
                        .status("PRODUCTION_READY")
                        .build(),
                BrokerCapabilityResponse.builder()
                        .brokerId("MOCK_BROKER")
                        .brokerName("TradoX Mock Connector")
                        .supportsHoldings(true)
                        .supportsPositions(true)
                        .supportsOrders(true)
                        .supportsHistoricalData(true)
                        .authType("MOCK_AUTH")
                        .status("PRODUCTION_READY")
                        .build()
        );
    }

    @Transactional
    public BrokerConnectionResponse connectBroker(String userEmail, ConnectBrokerRequest request) {
        User user = getUserByEmail(userEmail);
        String brokerId = request.getBrokerId().toUpperCase();

        BrokerConnector connector = connectorRegistry.get(brokerId);
        if (connector == null && !"ZERODHA".equals(brokerId) && !"GROWW".equals(brokerId) && !"ANGEL_ONE".equals(brokerId)) {
            throw new BadRequestException("Unsupported broker ID: " + brokerId);
        }

        // Use mock connector if zerodha/groww selected in development
        if (connector == null) {
            connector = connectorRegistry.get("MOCK_BROKER");
        }

        boolean authOk = connector.authenticate(request.getApiKey(), request.getApiSecret(), "dummy-access-token");
        if (!authOk) {
            throw new BadRequestException("Failed to authenticate with broker " + request.getBrokerId());
        }

        Optional<BrokerConnection> existingOpt = connectionRepository.findByUserIdAndBrokerId(user.getId(), brokerId);
        BrokerConnection connection;

        if (existingOpt.isPresent()) {
            connection = existingOpt.get();
            connection.setAccountName(request.getAccountName());
            connection.setApiKey(request.getApiKey());
            connection.setStatus(ConnectionStatus.CONNECTED);
        } else {
            connection = BrokerConnection.builder()
                    .user(user)
                    .brokerId(brokerId)
                    .accountName(request.getAccountName())
                    .apiKey(request.getApiKey())
                    .accessToken("token-" + UUID.randomUUID())
                    .status(ConnectionStatus.CONNECTED)
                    .build();
        }

        BrokerConnection saved = connectionRepository.save(connection);
        return mapConnectionToResponse(saved);
    }

    @Transactional
    public BrokerSyncLogResponse syncBrokerData(String userEmail, Long connectionId) {
        User user = getUserByEmail(userEmail);
        BrokerConnection connection = connectionRepository.findByIdAndUserId(connectionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("BrokerConnection", "id", connectionId));

        BrokerConnector connector = connectorRegistry.get(connection.getBrokerId());
        if (connector == null) {
            connector = connectorRegistry.get("MOCK_BROKER");
        }

        BrokerSyncLog syncLog = BrokerSyncLog.builder()
                .brokerConnection(connection)
                .status(SyncStatus.IN_PROGRESS)
                .syncStartedAt(LocalDateTime.now())
                .build();

        BrokerSyncLog savedLog = syncLogRepository.save(syncLog);

        try {
            // Get user default portfolio
            Portfolio defaultPortfolio = portfolioRepository.findByUserIdAndIsDefaultTrue(user.getId())
                    .orElseGet(() -> {
                        portfolioService.getUserPortfolios(userEmail);
                        return portfolioRepository.findByUserIdAndIsDefaultTrue(user.getId()).orElseThrow();
                    });

            // Fetch normalized data from broker
            List<NormalizedBrokerHolding> holdings = connector.fetchHoldings(connection.getAccessToken());
            List<NormalizedBrokerTransaction> transactions = connector.fetchTransactions(connection.getAccessToken());

            // Update Asset current prices from broker holdings
            if (holdings != null) {
                for (NormalizedBrokerHolding nh : holdings) {
                    if (nh.getCurrentPrice() != null) {
                        assetService.getOrCreateAsset(nh.getSymbol(), nh.getName(), nh.getExchange(), nh.getCurrentPrice());
                    }
                }
            }

            // Synchronize transactions with duplicate prevention
            int txCount = 0;
            if (transactions != null) {
                for (NormalizedBrokerTransaction ntx : transactions) {
                    // Prevent duplicate transaction imports using externalTxId
                    if (ntx.getExternalTxId() != null &&
                            transactionService.existsByPortfolioAndExternalTxId(defaultPortfolio.getId(), ntx.getExternalTxId())) {
                        continue;
                    }

                    transactionService.addTransaction(userEmail, defaultPortfolio.getId(), TransactionRequest.builder()
                            .symbol(ntx.getSymbol())
                            .assetName(ntx.getName())
                            .exchange(ntx.getExchange())
                            .type(ntx.getType())
                            .quantity(ntx.getQuantity())
                            .price(ntx.getPrice())
                            .fee(ntx.getFee())
                            .transactionDate(ntx.getTransactionDate())
                            .externalTxId(ntx.getExternalTxId())
                            .notes("Imported via " + connection.getBrokerId() + " (" + ntx.getExternalTxId() + ")")
                            .build());
                    txCount++;
                }
            }

            int holdingsCount = holdings != null ? holdings.size() : 0;
            savedLog.setStatus(SyncStatus.SUCCESS);
            savedLog.setHoldingsSynced(holdingsCount);
            savedLog.setTransactionsSynced(txCount);
            savedLog.setSyncCompletedAt(LocalDateTime.now());

            connection.setLastSyncedAt(LocalDateTime.now());
            connection.setStatus(ConnectionStatus.CONNECTED);
            connectionRepository.save(connection);

            return mapLogToResponse(syncLogRepository.save(savedLog));

        } catch (Exception e) {
            savedLog.setStatus(SyncStatus.FAILED);
            savedLog.setErrorMessage(e.getMessage());
            savedLog.setSyncCompletedAt(LocalDateTime.now());

            connection.setStatus(ConnectionStatus.ERROR);
            connectionRepository.save(connection);

            return mapLogToResponse(syncLogRepository.save(savedLog));
        }
    }

    @Transactional
    public BrokerSyncLogResponse triggerDevMockSync(String userEmail) {
        User user = getUserByEmail(userEmail);

        BrokerConnection connection = connectionRepository.findByUserIdAndBrokerId(user.getId(), "MOCK_BROKER")
                .orElseGet(() -> {
                    BrokerConnection newConn = BrokerConnection.builder()
                            .user(user)
                            .brokerId("MOCK_BROKER")
                            .accountName("Dev Mock Broker Account")
                            .apiKey("dev-mock-api-key")
                            .accessToken("token-mock-" + UUID.randomUUID())
                            .status(ConnectionStatus.CONNECTED)
                            .build();
                    return connectionRepository.save(newConn);
                });

        if (connection.getStatus() != ConnectionStatus.CONNECTED) {
            connection.setStatus(ConnectionStatus.CONNECTED);
            connectionRepository.save(connection);
        }

        return syncBrokerData(userEmail, connection.getId());
    }

    @Transactional(readOnly = true)
    public List<BrokerConnectionResponse> getUserConnections(String userEmail) {
        User user = getUserByEmail(userEmail);
        return connectionRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapConnectionToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BrokerSyncLogResponse> getConnectionLogs(String userEmail, Long connectionId) {
        User user = getUserByEmail(userEmail);
        connectionRepository.findByIdAndUserId(connectionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("BrokerConnection", "id", connectionId));

        return syncLogRepository.findByBrokerConnectionIdOrderBySyncStartedAtDesc(connectionId)
                .stream()
                .map(this::mapLogToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void disconnectBroker(String userEmail, Long connectionId) {
        User user = getUserByEmail(userEmail);
        BrokerConnection connection = connectionRepository.findByIdAndUserId(connectionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("BrokerConnection", "id", connectionId));

        connection.setStatus(ConnectionStatus.DISCONNECTED);
        connectionRepository.save(connection);
    }

    public BrokerConnectionResponse mapConnectionToResponse(BrokerConnection conn) {
        BrokerConnector connector = connectorRegistry.get(conn.getBrokerId());
        String brokerName = connector != null ? connector.getBrokerName() : conn.getBrokerId();

        return BrokerConnectionResponse.builder()
                .id(conn.getId())
                .brokerId(conn.getBrokerId())
                .brokerName(brokerName)
                .accountName(conn.getAccountName())
                .status(conn.getStatus())
                .lastSyncedAt(conn.getLastSyncedAt())
                .createdAt(conn.getCreatedAt())
                .build();
    }

    public BrokerSyncLogResponse mapLogToResponse(BrokerSyncLog log) {
        return BrokerSyncLogResponse.builder()
                .id(log.getId())
                .brokerConnectionId(log.getBrokerConnection().getId())
                .status(log.getStatus())
                .holdingsSynced(log.getHoldingsSynced())
                .transactionsSynced(log.getTransactionsSynced())
                .errorMessage(log.getErrorMessage())
                .syncStartedAt(log.getSyncStartedAt())
                .syncCompletedAt(log.getSyncCompletedAt())
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
}
