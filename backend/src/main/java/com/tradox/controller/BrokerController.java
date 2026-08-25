package com.tradox.controller;

import com.tradox.dto.request.ConnectBrokerRequest;
import com.tradox.dto.response.BrokerCapabilityResponse;
import com.tradox.dto.response.BrokerConnectionResponse;
import com.tradox.dto.response.BrokerSyncLogResponse;
import com.tradox.service.BrokerSyncService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brokers")
public class BrokerController {

    private final BrokerSyncService brokerSyncService;

    public BrokerController(BrokerSyncService brokerSyncService) {
        this.brokerSyncService = brokerSyncService;
    }

    /**
     * Get broker capability matrix.
     * GET /api/brokers/capabilities
     */
    @GetMapping("/capabilities")
    public ResponseEntity<List<BrokerCapabilityResponse>> getBrokerCapabilities() {
        return ResponseEntity.ok(brokerSyncService.getBrokerCapabilities());
    }

    /**
     * Get user connected broker accounts.
     * GET /api/brokers
     */
    @GetMapping
    public ResponseEntity<List<BrokerConnectionResponse>> getUserConnections(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(brokerSyncService.getUserConnections(userDetails.getUsername()));
    }

    /**
     * Connect a new broker account.
     * POST /api/brokers/connect
     */
    @PostMapping("/connect")
    public ResponseEntity<BrokerConnectionResponse> connectBroker(
            Authentication authentication,
            @Valid @RequestBody ConnectBrokerRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        BrokerConnectionResponse response = brokerSyncService.connectBroker(userDetails.getUsername(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Trigger manual broker data synchronization.
     * POST /api/brokers/{connectionId}/sync
     */
    @PostMapping("/{connectionId}/sync")
    public ResponseEntity<BrokerSyncLogResponse> syncBrokerData(
            Authentication authentication,
            @PathVariable Long connectionId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        BrokerSyncLogResponse response = brokerSyncService.syncBrokerData(userDetails.getUsername(), connectionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Development-only endpoint to trigger mock broker connection and synchronization.
     * POST /api/brokers/dev/sync
     */
    @PostMapping("/dev/sync")
    public ResponseEntity<BrokerSyncLogResponse> triggerDevMockSync(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        BrokerSyncLogResponse response = brokerSyncService.triggerDevMockSync(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Disconnect a broker account.
     * DELETE /api/brokers/{connectionId}
     */
    @DeleteMapping("/{connectionId}")
    public ResponseEntity<Void> disconnectBroker(
            Authentication authentication,
            @PathVariable Long connectionId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        brokerSyncService.disconnectBroker(userDetails.getUsername(), connectionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get synchronization audit logs for a connection.
     * GET /api/brokers/{connectionId}/logs
     */
    @GetMapping("/{connectionId}/logs")
    public ResponseEntity<List<BrokerSyncLogResponse>> getConnectionLogs(
            Authentication authentication,
            @PathVariable Long connectionId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(brokerSyncService.getConnectionLogs(userDetails.getUsername(), connectionId));
    }
}
