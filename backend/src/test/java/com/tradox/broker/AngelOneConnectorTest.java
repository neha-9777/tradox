package com.tradox.broker;

import com.tradox.broker.impl.AngelOneConnector;
import com.tradox.broker.model.NormalizedBrokerHolding;
import com.tradox.broker.model.NormalizedBrokerTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AngelOneConnectorTest {

    private AngelOneConnector connector;

    @BeforeEach
    void setUp() {
        connector = new AngelOneConnector();
    }

    @Test
    @DisplayName("Should verify Angel One connector metadata")
    void testMetadata() {
        assertEquals("ANGEL_ONE", connector.getBrokerId());
        assertEquals("Angel One SmartAPI", connector.getBrokerName());
    }

    @Test
    @DisplayName("Should authenticate Angel One credentials")
    void testAuthenticate() {
        assertTrue(connector.authenticate("ao_api_key", "secret", "token"));
        assertFalse(connector.authenticate("", "secret", "token"));
    }

    @Test
    @DisplayName("Should fetch and normalize Angel One holdings")
    void testFetchHoldings() {
        List<NormalizedBrokerHolding> holdings = connector.fetchHoldings("dummy_token");

        assertNotNull(holdings);
        assertEquals(2, holdings.size());
        assertEquals("TATAMOTORS", holdings.get(0).getSymbol());
        assertEquals("AXISBANK", holdings.get(1).getSymbol());
    }

    @Test
    @DisplayName("Should fetch and normalize Angel One SmartAPI orders")
    void testFetchTransactions() {
        List<NormalizedBrokerTransaction> transactions = connector.fetchTransactions("dummy_token");

        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        assertNotNull(transactions.get(0).getExternalTxId());
    }
}
