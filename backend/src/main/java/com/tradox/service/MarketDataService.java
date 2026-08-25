package com.tradox.service;

import com.tradox.dto.response.MarketQuoteResponse;
import com.tradox.dto.response.PriceHistoryPointResponse;
import com.tradox.entity.Asset;
import com.tradox.entity.Holding;
import com.tradox.entity.Portfolio;
import com.tradox.entity.User;
import com.tradox.exception.ResourceNotFoundException;
import com.tradox.repository.AssetRepository;
import com.tradox.repository.HoldingRepository;
import com.tradox.repository.PortfolioRepository;
import com.tradox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketDataService {

    private static final long CACHE_TTL_SECONDS = 300; // 5 Minutes TTL

    private final AssetRepository assetRepository;
    private final HoldingRepository holdingRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    // In-memory cache for market quotes
    private final ConcurrentHashMap<String, CachedQuote> quoteCache = new ConcurrentHashMap<>();

    public MarketDataService(AssetRepository assetRepository,
                             HoldingRepository holdingRepository,
                             PortfolioRepository portfolioRepository,
                             UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.holdingRepository = holdingRepository;
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get real-time or cached market quote for a symbol.
     */
    public MarketQuoteResponse getMarketQuote(String symbol) {
        String cleanSymbol = symbol.trim().toUpperCase();

        CachedQuote cached = quoteCache.get(cleanSymbol);
        if (cached != null && !cached.isExpired(CACHE_TTL_SECONDS)) {
            return cached.quote;
        }

        MarketQuoteResponse freshQuote = fetchMarketQuoteFromProvider(cleanSymbol);
        quoteCache.put(cleanSymbol, new CachedQuote(freshQuote, LocalDateTime.now()));
        return freshQuote;
    }

    /**
     * Get 30-day historical daily candle price data for a symbol.
     */
    public List<PriceHistoryPointResponse> getHistoricalPrices(String symbol, int days) {
        String cleanSymbol = symbol.trim().toUpperCase();
        MarketQuoteResponse currentQuote = getMarketQuote(cleanSymbol);

        BigDecimal basePrice = currentQuote.getCurrentPrice();
        List<PriceHistoryPointResponse> history = new ArrayList<>();
        LocalDate today = LocalDate.now();

        Random random = new Random(cleanSymbol.hashCode());

        BigDecimal runningPrice = basePrice;
        for (int i = days; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            // Random daily fluctuation (-2% to +2%)
            double factor = 1.0 + (random.nextDouble() * 0.04 - 0.02);
            BigDecimal dayClose = runningPrice.multiply(BigDecimal.valueOf(factor)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal dayOpen = dayClose.multiply(BigDecimal.valueOf(1.0 + (random.nextDouble() * 0.01 - 0.005))).setScale(2, RoundingMode.HALF_UP);
            BigDecimal dayHigh = dayClose.max(dayOpen).multiply(BigDecimal.valueOf(1.005)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal dayLow = dayClose.min(dayOpen).multiply(BigDecimal.valueOf(0.995)).setScale(2, RoundingMode.HALF_UP);

            history.add(PriceHistoryPointResponse.builder()
                    .date(date)
                    .closePrice(dayClose)
                    .openPrice(dayOpen)
                    .highPrice(dayHigh)
                    .lowPrice(dayLow)
                    .volume(100000L + random.nextInt(500000))
                    .build());

            runningPrice = dayClose;
        }

        return history;
    }

    /**
     * Refresh market prices for all holdings in a user portfolio.
     */
    @Transactional
    public List<Holding> refreshPortfolioHoldings(String userEmail, Long portfolioId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "id", portfolioId));

        List<Holding> holdings = holdingRepository.findByPortfolioIdOrderByTotalInvestedDesc(portfolio.getId());

        List<Holding> updatedHoldings = new ArrayList<>();
        for (Holding holding : holdings) {
            Asset asset = holding.getAsset();
            MarketQuoteResponse quote = getMarketQuote(asset.getSymbol());

            BigDecimal freshPrice = quote.getCurrentPrice();

            // Update Asset
            asset.setCurrentPrice(freshPrice);
            assetRepository.save(asset);

            // Update Holding
            holding.setCurrentPrice(freshPrice);
            BigDecimal val = freshPrice.multiply(holding.getQuantity());
            BigDecimal unrealized = val.subtract(holding.getTotalInvested());
            BigDecimal unrealizedPct = BigDecimal.ZERO;
            if (holding.getTotalInvested().compareTo(BigDecimal.ZERO) > 0) {
                unrealizedPct = unrealized.divide(holding.getTotalInvested(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            }

            holding.setCurrentValue(val);
            holding.setUnrealizedPnL(unrealized);
            holding.setUnrealizedPnLPercentage(unrealizedPct);

            updatedHoldings.add(holdingRepository.save(holding));
        }

        return updatedHoldings;
    }

    private MarketQuoteResponse fetchMarketQuoteFromProvider(String symbol) {
        Optional<Asset> assetOpt = assetRepository.findBySymbolIgnoreCase(symbol);

        BigDecimal basePrice = assetOpt
                .map(Asset::getCurrentPrice)
                .filter(p -> p != null && p.compareTo(BigDecimal.ZERO) > 0)
                .orElseGet(() -> estimateDefaultSymbolPrice(symbol));

        // Add subtle live tick fluctuation (+- 0.5%)
        double tickFactor = 1.0 + ((Math.random() * 0.01) - 0.005);
        BigDecimal currentPrice = basePrice.multiply(BigDecimal.valueOf(tickFactor)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal previousClose = basePrice;
        BigDecimal changeAmount = currentPrice.subtract(previousClose);
        BigDecimal changePct = changeAmount.divide(previousClose, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        return MarketQuoteResponse.builder()
                .symbol(symbol)
                .name(assetOpt.map(Asset::getName).orElse(symbol + " Corp"))
                .exchange(assetOpt.map(Asset::getExchange).orElse("NSE"))
                .currentPrice(currentPrice)
                .previousClose(previousClose)
                .changeAmount(changeAmount)
                .changePercentage(changePct)
                .dayHigh(currentPrice.multiply(BigDecimal.valueOf(1.01)).setScale(2, RoundingMode.HALF_UP))
                .dayLow(currentPrice.multiply(BigDecimal.valueOf(0.99)).setScale(2, RoundingMode.HALF_UP))
                .volume(1250000L)
                .dataSource("PROVIDER_SIMULATED_FEEDS")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private BigDecimal estimateDefaultSymbolPrice(String symbol) {
        switch (symbol.toUpperCase()) {
            case "RELIANCE": return new BigDecimal("2850.00");
            case "TCS": return new BigDecimal("3950.00");
            case "INFY": case "INFYS": return new BigDecimal("1645.00");
            case "HDFCBANK": return new BigDecimal("1680.00");
            case "ICICIBANK": return new BigDecimal("1120.00");
            case "TATAMOTORS": return new BigDecimal("980.00");
            case "AAPL": return new BigDecimal("225.00");
            case "TSLA": return new BigDecimal("210.00");
            default: return new BigDecimal("1500.00");
        }
    }

    private static class CachedQuote {
        MarketQuoteResponse quote;
        LocalDateTime cachedAt;

        CachedQuote(MarketQuoteResponse quote, LocalDateTime cachedAt) {
            this.quote = quote;
            this.cachedAt = cachedAt;
        }

        boolean isExpired(long ttlSeconds) {
            return LocalDateTime.now().isAfter(cachedAt.plusSeconds(ttlSeconds));
        }
    }
}
