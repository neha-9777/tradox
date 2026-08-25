package com.tradox.broker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalizedBrokerProfile {

    private String brokerUserId;
    private String name;
    private String email;
    private String brokerName;
}
