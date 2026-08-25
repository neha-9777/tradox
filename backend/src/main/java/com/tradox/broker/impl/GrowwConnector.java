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
public class GrowwConnector implements BrokerConnector {

    @Override
    public String getBrokerId() {
        return "GROWW";
    }

    @Override
    public String getBrokerName() {
        return "Groww India";
    }

    @Override
    public boolean authenticate(String apiKey, String apiSecret, String accessToken) {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public NormalizedBrokerProfile getProfile(String accessToken) {
        return NormalizedBrokerProfile.builder()
                .brokerUserId("GW-887711")
                .name("Priya Verma")
                .email("priya@groww-user.in")
                .brokerName(getBrokerName())
                .build();
    }

    @Override
    public List<NormalizedBrokerHolding> fetchHoldings(String accessToken) {
        List<NormalizedBrokerHolding> holdings = new ArrayList<>();

        holdings.add(NormalizedBrokerHolding.builder()
                .symbol("INFY")
                .name("Infosys Ltd")
                .exchange("NSE")
                .quantity(new BigDecimal("40"))
                .averagePrice(new BigDecimal("1420.00"))
                .currentPrice(new BigDecimal("1645.00"))
                .assetType(AssetType.EQUITY)
                .sector("Information Technology")
                .build());

        holdings.add(NormalizedBrokerHolding.builder()
                .symbol("ICICIBANK")
                .name("ICICI Bank Ltd")
                .exchange("NSE")
                .quantity(new BigDecimal("35"))
                .averagePrice(new BigDecimal("980.00"))
                .currentPrice(new BigDecimal("1120.00"))
                .assetType(AssetType.EQUITY)
                .sector("Banking")
                .build());

        return holdings;
    }

    @Override
    public List<NormalizedBrokerTransaction> fetchTransactions(String accessToken) {
        List<NormalizedBrokerTransaction> transactions = new ArrayList<>();

        transactions.add(NormalizedBrokerTransaction.builder()
                .externalTxId("GW-ORD-2024-881")
                .symbol("INFY")
                .name("Infosys Ltd")
                .exchange("NSE")
                .type(TransactionType.BUY)
                .quantity(new BigDecimal("40"))
                .price(new BigDecimal("1420.00"))
                .fee(new BigDecimal("20.00"))
                .transactionDate(LocalDateTime.now().minusDays(18))
                .build());

        transactions.add(NormalizedBrokerTransaction.builder()
                .externalTxId("GW-ORD-2024-882")
                .symbol("ICICIBANK")
                .name("ICICI Bank Ltd")
                .exchange("NSE")
                .type(TransactionType.BUY)
                .quantity(new BigDecimal("35"))
                .price(new BigDecimal("980.00"))
                .fee(new BigDecimal("25.00"))
                .transactionDate(LocalDateTime.now().minusDays(12))
                .build());

        return transactions;
    }

    @Override
    public void disconnect(String accessToken) {
        // Revoke Groww API token session
    }
}
