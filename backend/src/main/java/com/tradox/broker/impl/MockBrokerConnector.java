package com.tradox.broker.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tradox.broker.BrokerConnector;
import com.tradox.broker.model.NormalizedBrokerHolding;
import com.tradox.broker.model.NormalizedBrokerProfile;
import com.tradox.broker.model.NormalizedBrokerTransaction;
import com.tradox.entity.Asset.AssetType;
import com.tradox.entity.Transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MockBrokerConnector implements BrokerConnector {

    private static final String DEFAULT_MOCK_DATA_PATH = "mock-broker-data.json";
    private final ObjectMapper objectMapper;
    private String customMockDataPath;

    public MockBrokerConnector() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public MockBrokerConnector(String customMockDataPath) {
        this();
        this.customMockDataPath = customMockDataPath;
    }

    @Override
    public String getBrokerId() {
        return "MOCK_BROKER";
    }

    @Override
    public String getBrokerName() {
        return "TradoX Mock Broker Connector";
    }

    @Override
    public boolean authenticate(String apiKey, String apiSecret, String accessToken) {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public NormalizedBrokerProfile getProfile(String accessToken) {
        MockBrokerData data = loadMockData();
        if (data != null && data.getProfile() != null) {
            return data.getProfile();
        }
        return NormalizedBrokerProfile.builder()
                .brokerUserId("MB-998822")
                .name("Alex Trader")
                .email("alex@tradox-mock.com")
                .brokerName("Mock Broker Inc.")
                .build();
    }

    @Override
    public List<NormalizedBrokerHolding> fetchHoldings(String accessToken) {
        MockBrokerData data = loadMockData();
        if (data != null) {
            List<NormalizedBrokerHolding> combined = new ArrayList<>();
            if (data.getHoldings() != null) {
                combined.addAll(data.getHoldings());
            }
            if (data.getPositions() != null) {
                combined.addAll(data.getPositions());
            }
            if (!combined.isEmpty()) {
                return combined;
            }
        }
        return getFallbackHoldings();
    }

    @Override
    public List<NormalizedBrokerTransaction> fetchTransactions(String accessToken) {
        MockBrokerData data = loadMockData();
        if (data != null && data.getTransactions() != null && !data.getTransactions().isEmpty()) {
            return data.getTransactions();
        }
        return getFallbackTransactions();
    }

    @Override
    public void disconnect(String accessToken) {
        // Cleanup token session if applicable
    }

    public MockBrokerData loadMockData() {
        String path = customMockDataPath != null ? customMockDataPath : DEFAULT_MOCK_DATA_PATH;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is != null) {
                return objectMapper.readValue(is, MockBrokerData.class);
            }
        } catch (Exception e) {
            // Fallback silently if reading fails
        }
        return null;
    }

    private List<NormalizedBrokerHolding> getFallbackHoldings() {
        return List.of(
                NormalizedBrokerHolding.builder()
                        .symbol("RELIANCE")
                        .name("Reliance Industries Ltd")
                        .exchange("NSE")
                        .quantity(new BigDecimal("20"))
                        .averagePrice(new BigDecimal("2450.00"))
                        .currentPrice(new BigDecimal("2850.00"))
                        .assetType(AssetType.EQUITY)
                        .sector("Energy")
                        .build(),
                NormalizedBrokerHolding.builder()
                        .symbol("TCS")
                        .name("Tata Consultancy Services")
                        .exchange("NSE")
                        .quantity(new BigDecimal("15"))
                        .averagePrice(new BigDecimal("3500.00"))
                        .currentPrice(new BigDecimal("4100.00"))
                        .assetType(AssetType.EQUITY)
                        .sector("Information Technology")
                        .build()
        );
    }

    private List<NormalizedBrokerTransaction> getFallbackTransactions() {
        return List.of(
                NormalizedBrokerTransaction.builder()
                        .externalTxId("TX-MB-101")
                        .symbol("RELIANCE")
                        .name("Reliance Industries Ltd")
                        .exchange("NSE")
                        .type(TransactionType.BUY)
                        .quantity(new BigDecimal("20"))
                        .price(new BigDecimal("2450.00"))
                        .fee(new BigDecimal("25.00"))
                        .transactionDate(LocalDateTime.now().minusDays(15))
                        .build()
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MockBrokerData {
        private NormalizedBrokerProfile profile;
        private List<NormalizedBrokerHolding> holdings;
        private List<NormalizedBrokerHolding> positions;
        private List<NormalizedBrokerTransaction> transactions;
    }
}
