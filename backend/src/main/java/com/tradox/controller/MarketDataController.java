package com.tradox.controller;

import com.tradox.dto.response.MarketQuoteResponse;
import com.tradox.dto.response.PriceHistoryPointResponse;
import com.tradox.service.MarketDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    /**
     * Get real-time or cached market quote for a symbol.
     * GET /api/market/quote/{symbol}
     */
    @GetMapping("/quote/{symbol}")
    public ResponseEntity<MarketQuoteResponse> getMarketQuote(@PathVariable String symbol) {
        return ResponseEntity.ok(marketDataService.getMarketQuote(symbol));
    }

    /**
     * Get 30-day historical daily candle price data for a symbol.
     * GET /api/market/history/{symbol}
     */
    @GetMapping("/history/{symbol}")
    public ResponseEntity<List<PriceHistoryPointResponse>> getHistoricalPrices(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(marketDataService.getHistoricalPrices(symbol, days));
    }

    /**
     * Trigger fresh market price sync for all holdings in a portfolio.
     * POST /api/market/portfolios/{portfolioId}/refresh
     */
    @PostMapping("/portfolios/{portfolioId}/refresh")
    public ResponseEntity<Void> refreshPortfolioHoldings(
            Authentication authentication,
            @PathVariable Long portfolioId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        marketDataService.refreshPortfolioHoldings(userDetails.getUsername(), portfolioId);
        return ResponseEntity.ok().build();
    }
}
