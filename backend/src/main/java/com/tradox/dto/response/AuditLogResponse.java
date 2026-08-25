package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;
    private String actionType;
    private String resourceName;
    private String ipAddress;
    private String status;
    private String details;
    private LocalDateTime timestamp;
}
