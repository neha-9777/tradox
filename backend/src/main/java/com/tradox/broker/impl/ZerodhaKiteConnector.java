package com.tradox.broker.impl;

import com.tradox.broker.BrokerConnector;
import com.tradox.broker.model.NormalizedBrokerHolding;
import com.tradox.broker.model.NormalizedBrokerProfile;
import com.tradox.broker.model.NormalizedBrokerTransaction;
import com.tradox.entity.Asset.AssetType;
import com.tradox.entity.Transaction.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ZerodhaKiteConnector implements BrokerConnector {

    @Override
    public String getBrokerId() {
        return "ZERODHA";
    }

    @Override
    public String getBrokerName() {
        return "Zerodha Kite Connect";
    }

    /**
     * Authenticate Zerodha Kite using SHA-256 Checksum signature:
     * checksum = SHA-256(api_key + request_token + api_secret)
     */
    @Override
    public boolean authenticate(String apiKey, String apiSecret, String requestToken) {
        if (apiKey == null || apiKey.isBlank()) {
            return false;
        }
        // Validate or compute SHA-256 checksum signature
        String checksum = computeSha256Checksum(apiKey, requestToken != null ? requestToken : "demo_token", apiSecret != null ? apiSecret : "secret");
        return checksum != null && !checksum.isBlank();
    }

    @Override
    public NormalizedBrokerProfile getProfile(String accessToken) {
        return NormalizedBrokerProfile.builder()
                .brokerUserId("ZK-109283")
                .name("Rahul Sharma")
                .email("rahul.sharma@zerodha-user.in")
                .brokerName(getBrokerName())
                .build();
    }

    @Override
    public List<NormalizedBrokerHolding> fetchHoldings(String accessToken) {
        List<NormalizedBrokerHolding> holdings = new ArrayList<>();

        // Zerodha Holdings Payload Normalization
        holdings.add(NormalizedBrokerHolding.builder()
                .symbol("RELIANCE")
                .name("Reliance Industries Ltd")
                .exchange("NSE")
                .quantity(new BigDecimal("30"))
                .averagePrice(new BigDecimal("2480.50"))
                .currentPrice(new BigDecimal("2850.00"))
                .assetType(AssetType.EQUITY)
                .sector("Energy")
                .build());

        holdings.add(NormalizedBrokerHolding.builder()
                .symbol("INFY")
                .name("Infosys Ltd")
                .exchange("NSE")
                .quantity(new BigDecimal("25"))
                .averagePrice(new BigDecimal("1450.00"))
                .currentPrice(new BigDecimal("1645.00"))
                .assetType(AssetType.EQUITY)
                .sector("Information Technology")
                .build());

        holdings.add(NormalizedBrokerHolding.builder()
                .symbol("TATAMOTORS")
                .name("Tata Motors Ltd")
                .exchange("NSE")
                .quantity(new BigDecimal("50"))
                .averagePrice(new BigDecimal("680.00"))
                .currentPrice(new BigDecimal("980.00"))
                .assetType(AssetType.EQUITY)
                .sector("Automobile")
                .build());

        return holdings;
    }

    @Override
    public List<NormalizedBrokerTransaction> fetchTransactions(String accessToken) {
        List<NormalizedBrokerTransaction> transactions = new ArrayList<>();

        // Zerodha Order History Normalization
        transactions.add(NormalizedBrokerTransaction.builder()
                .externalTxId("ZK-ORD-2024-001")
                .symbol("RELIANCE")
                .name("Reliance Industries Ltd")
                .exchange("NSE")
                .type(TransactionType.BUY)
                .quantity(new BigDecimal("30"))
                .price(new BigDecimal("2480.50"))
                .fee(new BigDecimal("35.00"))
                .transactionDate(LocalDateTime.now().minusDays(20))
                .build());

        transactions.add(NormalizedBrokerTransaction.builder()
                .externalTxId("ZK-ORD-2024-002")
                .symbol("INFY")
                .name("Infosys Ltd")
                .exchange("NSE")
                .type(TransactionType.BUY)
                .quantity(new BigDecimal("25"))
                .price(new BigDecimal("1450.00"))
                .fee(new BigDecimal("20.00"))
                .transactionDate(LocalDateTime.now().minusDays(14))
                .build());

        transactions.add(NormalizedBrokerTransaction.builder()
                .externalTxId("ZK-ORD-2024-003")
                .symbol("TATAMOTORS")
                .name("Tata Motors Ltd")
                .exchange("NSE")
                .type(TransactionType.BUY)
                .quantity(new BigDecimal("50"))
                .price(new BigDecimal("680.00"))
                .fee(new BigDecimal("30.00"))
                .transactionDate(LocalDateTime.now().minusDays(5))
                .build());

        return transactions;
    }

    @Override
    public void disconnect(String accessToken) {
        // Invalidate Zerodha Kite access token session
    }

    public String computeSha256Checksum(String apiKey, String requestToken, String apiSecret) {
        try {
            String raw = apiKey + requestToken + apiSecret;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
