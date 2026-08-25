package com.tradox.broker.impl;

import com.tradox.broker.BrokerConnector;
import com.tradox.broker.model.NormalizedBrokerHolding;
import com.tradox.broker.model.NormalizedBrokerProfile;
import com.tradox.broker.model.NormalizedBrokerTransaction;
import com.tradox.entity.Asset.AssetType;
import com.tradox.entity.Transaction.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class AngelOneConnector implements BrokerConnector {

    @Override
    public String getBrokerId() {
        return "ANGEL_ONE";
    }

    @Override
    public String getBrokerName() {
        return "Angel One SmartAPI";
    }

    @Override
    public boolean authenticate(String apiKey, String apiSecret, String accessToken) {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public NormalizedBrokerProfile getProfile(String accessToken) {
        return NormalizedBrokerProfile.builder()
                .brokerUserId("AO-554433")
                .name("Vikram Singh")
                .email("vikram@angelone-user.in")
                .brokerName(getBrokerName())
                .build();
    }

    @Override
    public List<NormalizedBrokerHolding> fetchHoldings(String accessToken) {
        List<NormalizedBrokerHolding> holdings = new ArrayList<>();

        holdings.add(NormalizedBrokerHolding.builder()
                .symbol("TATAMOTORS")
                .name("Tata Motors Ltd")
                .exchange("NSE")
                .quantity(new BigDecimal("30"))
                .averagePrice(new BigDecimal("710.00"))
                .currentPrice(new BigDecimal("980.00"))
                .assetType(AssetType.EQUITY)
                .sector("Automobile")
                .build());

        holdings.add(NormalizedBrokerHolding.builder()
                .symbol("AXISBANK")
                .name("Axis Bank Ltd")
                .exchange("NSE")
                .quantity(new BigDecimal("25"))
                .averagePrice(new BigDecimal("1020.00"))
                .currentPrice(new BigDecimal("1180.00"))
                .assetType(AssetType.EQUITY)
                .sector("Banking")
                .build());

        return holdings;
    }

    @Override
    public List<NormalizedBrokerTransaction> fetchTransactions(String accessToken) {
        List<NormalizedBrokerTransaction> transactions = new ArrayList<>();

        transactions.add(NormalizedBrokerTransaction.builder()
                .externalTxId("AO-ORD-2024-551")
                .symbol("TATAMOTORS")
                .name("Tata Motors Ltd")
                .exchange("NSE")
                .type(TransactionType.BUY)
                .quantity(new BigDecimal("30"))
                .price(new BigDecimal("710.00"))
                .fee(new BigDecimal("18.00"))
                .transactionDate(LocalDateTime.now().minusDays(22))
                .build());

        transactions.add(NormalizedBrokerTransaction.builder()
                .externalTxId("AO-ORD-2024-552")
                .symbol("AXISBANK")
                .name("Axis Bank Ltd")
                .exchange("NSE")
                .type(TransactionType.BUY)
                .quantity(new BigDecimal("25"))
                .price(new BigDecimal("1020.00"))
                .fee(new BigDecimal("22.00"))
                .transactionDate(LocalDateTime.now().minusDays(15))
                .build());

        return transactions;
    }

    @Override
    public void disconnect(String accessToken) {
        // Revoke Angel One SmartAPI session token
    }
}
