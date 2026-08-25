package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrokerCapabilityResponse {

    private String brokerId;
    private String brokerName;
    private Boolean supportsHoldings;
    private Boolean supportsPositions;
    private Boolean supportsOrders;
    private Boolean supportsHistoricalData;
    private String authType; // SHA256_CHECKSUM, OAUTH2, API_KEY_SECRET
    private String status; // PRODUCTION_READY, BETA
}
