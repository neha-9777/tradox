package com.tradox.controller;

import com.tradox.dto.response.PnLResponse;
import com.tradox.service.PnLEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pnl")
public class PnLController {

    private final PnLEngineService pnlEngineService;

    public PnLController(PnLEngineService pnlEngineService) {
        this.pnlEngineService = pnlEngineService;
    }

    /**
     * Get comprehensive P&L summary and breakdown for authenticated user.
     * GET /api/pnl/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<PnLResponse> getUserPnLSummary(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        PnLResponse response = pnlEngineService.getUserPnLSummary(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
