package com.tradox.broker;

import com.tradox.broker.impl.GrowwConnector;
import com.tradox.broker.model.NormalizedBrokerHolding;
import com.tradox.broker.model.NormalizedBrokerTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GrowwConnectorTest {

    private GrowwConnector connector;

    @BeforeEach
    void setUp() {
        connector = new GrowwConnector();
    }

    @Test
    @DisplayName("Should verify Groww connector metadata")
    void testMetadata() {
        assertEquals("GROWW", connector.getBrokerId());
        assertEquals("Groww India", connector.getBrokerName());
    }

    @Test
    @DisplayName("Should authenticate Groww credentials")
    void testAuthenticate() {
        assertTrue(connector.authenticate("gw_api_key", "secret", "token"));
        assertFalse(connector.authenticate("", "secret", "token"));
    }

    @Test
    @DisplayName("Should fetch and normalize Groww holdings")
    void testFetchHoldings() {
        List<NormalizedBrokerHolding> holdings = connector.fetchHoldings("dummy_token");

        assertNotNull(holdings);
        assertEquals(2, holdings.size());
        assertEquals("INFY", holdings.get(0).getSymbol());
        assertEquals("ICICIBANK", holdings.get(1).getSymbol());
    }

    @Test
    @DisplayName("Should fetch and normalize Groww executed orders")
    void testFetchTransactions() {
        List<NormalizedBrokerTransaction> transactions = connector.fetchTransactions("dummy_token");

        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        assertNotNull(transactions.get(0).getExternalTxId());
    }
}
