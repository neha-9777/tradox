package com.tradox.broker;

import com.tradox.broker.impl.ZerodhaKiteConnector;
import com.tradox.broker.model.NormalizedBrokerHolding;
import com.tradox.broker.model.NormalizedBrokerProfile;
import com.tradox.broker.model.NormalizedBrokerTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZerodhaKiteConnectorTest {

    private ZerodhaKiteConnector connector;

    @BeforeEach
    void setUp() {
        connector = new ZerodhaKiteConnector();
    }

    @Test
    @DisplayName("Should verify Zerodha connector metadata")
    void testMetadata() {
        assertEquals("ZERODHA", connector.getBrokerId());
        assertEquals("Zerodha Kite Connect", connector.getBrokerName());
    }

    @Test
    @DisplayName("Should compute valid SHA-256 checksum for Zerodha authentication")
    void testComputeSha256Checksum() {
        String apiKey = "test_api_key";
        String requestToken = "req_token_123";
        String apiSecret = "secret_abc";

        String checksum = connector.computeSha256Checksum(apiKey, requestToken, apiSecret);

        assertNotNull(checksum);
        assertEquals(64, checksum.length()); // SHA-256 hex string length is 64 chars
    }

    @Test
    @DisplayName("Should authenticate successfully with valid API key")
    void testAuthenticate_ValidApiKey() {
        boolean ok = connector.authenticate("valid_api_key", "secret", "req_token");
        assertTrue(ok);
    }

    @Test
    @DisplayName("Should fail authentication when API key is empty or null")
    void testAuthenticate_InvalidApiKey() {
        assertFalse(connector.authenticate("", "secret", "req_token"));
        assertFalse(connector.authenticate(null, "secret", "req_token"));
    }

    @Test
    @DisplayName("Should fetch and normalize Zerodha user profile")
    void testGetProfile() {
        NormalizedBrokerProfile profile = connector.getProfile("dummy_token");

        assertNotNull(profile);
        assertEquals("Zerodha Kite Connect", profile.getBrokerName());
        assertNotNull(profile.getBrokerUserId());
        assertNotNull(profile.getEmail());
    }

    @Test
    @DisplayName("Should fetch and normalize Zerodha holdings data")
    void testFetchHoldings_Normalization() {
        List<NormalizedBrokerHolding> holdings = connector.fetchHoldings("dummy_token");

        assertNotNull(holdings);
        assertFalse(holdings.isEmpty());

        NormalizedBrokerHolding firstHolding = holdings.get(0);
        assertEquals("RELIANCE", firstHolding.getSymbol());
        assertEquals("NSE", firstHolding.getExchange());
        assertTrue(firstHolding.getQuantity().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(firstHolding.getAveragePrice().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should fetch and normalize Zerodha order history transactions")
    void testFetchTransactions_Normalization() {
        List<NormalizedBrokerTransaction> transactions = connector.fetchTransactions("dummy_token");

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());

        NormalizedBrokerTransaction firstTx = transactions.get(0);
        assertNotNull(firstTx.getExternalTxId());
        assertEquals("RELIANCE", firstTx.getSymbol());
        assertNotNull(firstTx.getType());
        assertTrue(firstTx.getPrice().compareTo(BigDecimal.ZERO) > 0);
    }
}
