package com.tradox.dto.response;

import com.tradox.entity.BrokerConnection.ConnectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrokerConnectionResponse {

    private Long id;
    private String brokerId;
    private String brokerName;
    private String accountName;
    private ConnectionStatus status;
    private LocalDateTime lastSyncedAt;
    private LocalDateTime createdAt;
}
