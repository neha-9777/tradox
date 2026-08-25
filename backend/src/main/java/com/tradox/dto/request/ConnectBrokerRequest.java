package com.tradox.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectBrokerRequest {

    @NotBlank(message = "Broker ID is required")
    private String brokerId;

    @NotBlank(message = "Account name is required")
    private String accountName;

    private String apiKey;

    private String apiSecret;
}
