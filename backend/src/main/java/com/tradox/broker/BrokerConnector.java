package com.tradox.broker;

import com.tradox.broker.model.NormalizedBrokerHolding;
import com.tradox.broker.model.NormalizedBrokerProfile;
import com.tradox.broker.model.NormalizedBrokerTransaction;

import java.util.List;

public interface BrokerConnector {

    String getBrokerId();

    String getBrokerName();

    boolean authenticate(String apiKey, String apiSecret, String accessToken);

    NormalizedBrokerProfile getProfile(String accessToken);

    List<NormalizedBrokerHolding> fetchHoldings(String accessToken);

    List<NormalizedBrokerTransaction> fetchTransactions(String accessToken);

    void disconnect(String accessToken);
}
