package com.tradox.service;

import com.tradox.dto.response.AuditLogResponse;
import com.tradox.entity.AuditLog;
import com.tradox.entity.User;
import com.tradox.exception.ResourceNotFoundException;
import com.tradox.repository.AuditLogRepository;
import com.tradox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void logEvent(String userEmail, String actionType, String resourceName, String status, String details) {
        User user = null;
        if (userEmail != null && !userEmail.isBlank()) {
            user = userRepository.findByEmail(userEmail).orElse(null);
        }

        AuditLog log = AuditLog.builder()
                .user(user)
                .actionType(actionType)
                .resourceName(resourceName)
                .ipAddress("127.0.0.1")
                .status(status)
                .details(details)
                .build();

        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getUserAuditLogs(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        return auditLogRepository.findByUserIdOrderByTimestampDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .actionType(log.getActionType())
                .resourceName(log.getResourceName())
                .ipAddress(log.getIpAddress())
                .status(log.getStatus())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}
