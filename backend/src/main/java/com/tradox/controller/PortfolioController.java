package com.tradox.controller;

import com.tradox.dto.request.PortfolioRequest;
import com.tradox.dto.request.TransactionRequest;
import com.tradox.dto.response.PortfolioResponse;
import com.tradox.dto.response.PortfolioSummaryResponse;
import com.tradox.dto.response.TransactionResponse;
import com.tradox.service.PortfolioService;
import com.tradox.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final TransactionService transactionService;

    public PortfolioController(PortfolioService portfolioService, TransactionService transactionService) {
        this.portfolioService = portfolioService;
        this.transactionService = transactionService;
    }

    /**
     * Get all portfolios for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> getUserPortfolios(Authentication authentication) {
        String email = getEmail(authentication);
        return ResponseEntity.ok(portfolioService.getUserPortfolios(email));
    }

    /**
     * Create a new portfolio.
     */
    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(Authentication authentication,
                                                            @Valid @RequestBody PortfolioRequest request) {
        String email = getEmail(authentication);
        PortfolioResponse response = portfolioService.createPortfolio(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get aggregated portfolio summary for the authenticated user.
     */
    @GetMapping("/summary")
    public ResponseEntity<PortfolioSummaryResponse> getOverallSummary(Authentication authentication) {
        String email = getEmail(authentication);
        return ResponseEntity.ok(portfolioService.getOverallSummary(email));
    }

    /**
     * Get all transactions for the authenticated user across all portfolios.
     */
    @GetMapping("/transactions/all")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(Authentication authentication) {
        String email = getEmail(authentication);
        return ResponseEntity.ok(transactionService.getAllUserTransactions(email));
    }

    /**
     * Get portfolio details by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> getPortfolioById(Authentication authentication,
                                                              @PathVariable Long id) {
        String email = getEmail(authentication);
        return ResponseEntity.ok(portfolioService.getPortfolioById(email, id));
    }

    /**
     * Update portfolio.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioResponse> updatePortfolio(Authentication authentication,
                                                              @PathVariable Long id,
                                                              @Valid @RequestBody PortfolioRequest request) {
        String email = getEmail(authentication);
        return ResponseEntity.ok(portfolioService.updatePortfolio(email, id, request));
    }

    /**
     * Delete portfolio.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(Authentication authentication,
                                                @PathVariable Long id) {
        String email = getEmail(authentication);
        portfolioService.deletePortfolio(email, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get transactions for a specific portfolio.
     */
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(Authentication authentication,
                                                                      @PathVariable Long id) {
        String email = getEmail(authentication);
        return ResponseEntity.ok(transactionService.getTransactionsByPortfolio(email, id));
    }

    /**
     * Add a new transaction to a portfolio.
     */
    @PostMapping("/{id}/transactions")
    public ResponseEntity<TransactionResponse> addTransaction(Authentication authentication,
                                                               @PathVariable Long id,
                                                               @Valid @RequestBody TransactionRequest request) {
        String email = getEmail(authentication);
        TransactionResponse response = transactionService.addTransaction(email, id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Delete a transaction from a portfolio.
     */
    @DeleteMapping("/{id}/transactions/{txId}")
    public ResponseEntity<Void> deleteTransaction(Authentication authentication,
                                                  @PathVariable Long id,
                                                  @PathVariable Long txId) {
        String email = getEmail(authentication);
        transactionService.deleteTransaction(email, id, txId);
        return ResponseEntity.noContent().build();
    }

    private String getEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
