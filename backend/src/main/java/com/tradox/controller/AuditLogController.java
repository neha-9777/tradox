package com.tradox.controller;

import com.tradox.dto.response.AuditLogResponse;
import com.tradox.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * Get security audit trail for authenticated user.
     * GET /api/audit-logs
     */
    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getUserAuditLogs(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(auditLogService.getUserAuditLogs(userDetails.getUsername()));
    }
}
