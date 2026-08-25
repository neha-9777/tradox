package com.tradox.dto.response;

import com.tradox.entity.BrokerSyncLog.SyncStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrokerSyncLogResponse {

    private Long id;
    private Long brokerConnectionId;
    private SyncStatus status;
    private Integer holdingsSynced;
    private Integer transactionsSynced;
    private String errorMessage;
    private LocalDateTime syncStartedAt;
    private LocalDateTime syncCompletedAt;
}
