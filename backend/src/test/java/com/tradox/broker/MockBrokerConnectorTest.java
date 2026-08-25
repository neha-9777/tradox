package com.tradox.broker;

import com.tradox.broker.impl.MockBrokerConnector;
import com.tradox.broker.model.NormalizedBrokerHolding;
import com.tradox.broker.model.NormalizedBrokerProfile;
import com.tradox.broker.model.NormalizedBrokerTransaction;
import com.tradox.entity.Transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockBrokerConnectorTest {

    private MockBrokerConnector mockBrokerConnector;

    @BeforeEach
    void setUp() {
        mockBrokerConnector = new MockBrokerConnector();
    }

    @Test
    @DisplayName("Should return broker info and authenticate correctly")
    void testBrokerMetadataAndAuth() {
        assertEquals("MOCK_BROKER", mockBrokerConnector.getBrokerId());
        assertEquals("TradoX Mock Broker Connector", mockBrokerConnector.getBrokerName());
        assertTrue(mockBrokerConnector.authenticate("valid-key", "secret", "token"));
        assertFalse(mockBrokerConnector.authenticate("", "secret", "token"));
    }

    @Test
    @DisplayName("Should load profile from JSON mock file")
    void testGetProfile() {
        NormalizedBrokerProfile profile = mockBrokerConnector.getProfile("token-123");
        assertNotNull(profile);
        assertEquals("MB-998822", profile.getBrokerUserId());
        assertEquals("Alex Trader", profile.getName());
        assertEquals("alex@tradox-mock.com", profile.getEmail());
    }

    @Test
    @DisplayName("Should load holdings and positions from JSON mock file")
    void testFetchHoldings() {
        List<NormalizedBrokerHolding> holdings = mockBrokerConnector.fetchHoldings("token-123");
        assertNotNull(holdings);
        assertFalse(holdings.isEmpty());

        // Verify holdings contain expected symbols from JSON (RELIANCE, TCS, TATASTEEL, HDFCBANK, INFOSYS)
        assertTrue(holdings.stream().anyMatch(h -> "RELIANCE".equals(h.getSymbol())));
        assertTrue(holdings.stream().anyMatch(h -> "TCS".equals(h.getSymbol())));
        assertTrue(holdings.stream().anyMatch(h -> "TATASTEEL".equals(h.getSymbol())));
        assertTrue(holdings.stream().anyMatch(h -> "INFOSYS".equals(h.getSymbol())));
    }

    @Test
    @DisplayName("Should load transactions from JSON mock file and verify scenarios")
    void testFetchTransactions_Scenarios() {
        List<NormalizedBrokerTransaction> transactions = mockBrokerConnector.fetchTransactions("token-123");
        assertNotNull(transactions);
        assertEquals(7, transactions.size());

        // Multiple Buys Scenario (RELIANCE)
        List<NormalizedBrokerTransaction> relianceTxs = transactions.stream()
                .filter(t -> "RELIANCE".equals(t.getSymbol()))
                .toList();
        assertEquals(2, relianceTxs.size());
        assertTrue(relianceTxs.stream().allMatch(t -> t.getType() == TransactionType.BUY));

        // Partial Sell & Profit Scenario (TCS)
        List<NormalizedBrokerTransaction> tcsTxs = transactions.stream()
                .filter(t -> "TCS".equals(t.getSymbol()))
                .toList();
        assertEquals(2, tcsTxs.size());
        NormalizedBrokerTransaction tcsBuy = tcsTxs.stream().filter(t -> t.getType() == TransactionType.BUY).findFirst().orElseThrow();
        NormalizedBrokerTransaction tcsSell = tcsTxs.stream().filter(t -> t.getType() == TransactionType.SELL).findFirst().orElseThrow();
        assertEquals(new BigDecimal("20"), tcsBuy.getQuantity());
        assertEquals(new BigDecimal("5"), tcsSell.getQuantity());
        assertTrue(tcsSell.getPrice().compareTo(tcsBuy.getPrice()) > 0); // Profit sell

        // Loss Scenario (TATASTEEL)
        List<NormalizedBrokerTransaction> steelTxs = transactions.stream()
                .filter(t -> "TATASTEEL".equals(t.getSymbol()))
                .toList();
        assertEquals(2, steelTxs.size());
        NormalizedBrokerTransaction steelBuy = steelTxs.stream().filter(t -> t.getType() == TransactionType.BUY).findFirst().orElseThrow();
        NormalizedBrokerTransaction steelSell = steelTxs.stream().filter(t -> t.getType() == TransactionType.SELL).findFirst().orElseThrow();
        assertTrue(steelSell.getPrice().compareTo(steelBuy.getPrice()) < 0); // Loss sell
    }
}
