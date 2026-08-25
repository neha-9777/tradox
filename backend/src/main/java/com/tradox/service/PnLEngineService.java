package com.tradox.service;

import com.tradox.dto.response.AssetPnLResponse;
import com.tradox.dto.response.PnLResponse;
import com.tradox.entity.Asset;
import com.tradox.entity.Holding;
import com.tradox.entity.Portfolio;
import com.tradox.entity.Transaction;
import com.tradox.entity.Transaction.TransactionType;
import com.tradox.entity.User;
import com.tradox.exception.ResourceNotFoundException;
import com.tradox.repository.HoldingRepository;
import com.tradox.repository.PortfolioRepository;
import com.tradox.repository.TransactionRepository;
import com.tradox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PnLEngineService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;

    public PnLEngineService(UserRepository userRepository,
                            PortfolioRepository portfolioRepository,
                            HoldingRepository holdingRepository,
                            TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Calculate Realized P&L for a single SELL transaction.
     * Formula: (sellPrice - avgCostPrice) * sellQty - fee
     */
    public BigDecimal calculateRealizedPnL(BigDecimal sellQty, BigDecimal sellPrice, BigDecimal avgCostPrice, BigDecimal fee) {
        if (sellQty == null || sellPrice == null || avgCostPrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal actualFee = fee != null ? fee : BigDecimal.ZERO;
        BigDecimal grossProfit = sellPrice.subtract(avgCostPrice).multiply(sellQty);
        return grossProfit.subtract(actualFee);
    }

    /**
     * Calculate Unrealized P&L for a holding position.
     * Formula: (currentPrice - avgBuyPrice) * quantity
     */
    public BigDecimal calculateUnrealizedPnL(BigDecimal quantity, BigDecimal avgBuyPrice, BigDecimal currentPrice) {
        if (quantity == null || avgBuyPrice == null || currentPrice == null) {
            return BigDecimal.ZERO;
        }
        return currentPrice.subtract(avgBuyPrice).multiply(quantity);
    }

    /**
     * Calculate Return Percentage.
     * Formula: ((currentValue + totalRealizedPnL - totalInvested) / totalInvested) * 100
     */
    public BigDecimal calculateReturnPercentage(BigDecimal totalInvested, BigDecimal currentValue, BigDecimal totalRealizedPnL) {
        if (totalInvested == null || totalInvested.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal val = currentValue != null ? currentValue : BigDecimal.ZERO;
        BigDecimal realized = totalRealizedPnL != null ? totalRealizedPnL : BigDecimal.ZERO;

        BigDecimal netGain = val.add(realized).subtract(totalInvested);
        return netGain.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calculate aggregated P&L metrics for the authenticated user.
     */
    @Transactional(readOnly = true)
    public PnLResponse getUserPnLSummary(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        List<Portfolio> portfolios = portfolioRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        List<Transaction> allTransactions = transactionRepository.findByPortfolioUserIdOrderByTransactionDateDesc(user.getId());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        BigDecimal totalRealizedPnL = BigDecimal.ZERO;
        BigDecimal dailyPnL = BigDecimal.ZERO;
        BigDecimal weeklyPnL = BigDecimal.ZERO;
        BigDecimal monthlyPnL = BigDecimal.ZERO;

        // Map to track asset-level realized PnL
        Map<Long, BigDecimal> assetRealizedMap = new HashMap<>();
        Map<Long, Asset> assetMap = new HashMap<>();

        for (Transaction tx : allTransactions) {
            if (tx.getType() == TransactionType.SELL && tx.getRealizedPnL() != null) {
                BigDecimal pnl = tx.getRealizedPnL();
                totalRealizedPnL = totalRealizedPnL.add(pnl);

                // Timeframe checks
                if (!tx.getTransactionDate().isBefore(todayStart)) {
                    dailyPnL = dailyPnL.add(pnl);
                }
                if (!tx.getTransactionDate().isBefore(sevenDaysAgo)) {
                    weeklyPnL = weeklyPnL.add(pnl);
                }
                if (!tx.getTransactionDate().isBefore(thirtyDaysAgo)) {
                    monthlyPnL = monthlyPnL.add(pnl);
                }

                // Asset breakdown
                Asset asset = tx.getAsset();
                assetMap.put(asset.getId(), asset);
                assetRealizedMap.put(asset.getId(), assetRealizedMap.getOrDefault(asset.getId(), BigDecimal.ZERO).add(pnl));
            }
        }

        // Process holdings across all portfolios
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;
        BigDecimal totalUnrealizedPnL = BigDecimal.ZERO;

        Map<Long, HoldingAggregator> holdingAggMap = new HashMap<>();

        for (Portfolio p : portfolios) {
            List<Holding> holdings = holdingRepository.findByPortfolioIdOrderByTotalInvestedDesc(p.getId());
            for (Holding h : holdings) {
                if (h.getQuantity() != null && h.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    Asset asset = h.getAsset();
                    assetMap.put(asset.getId(), asset);

                    BigDecimal price = h.getCurrentPrice() != null && h.getCurrentPrice().compareTo(BigDecimal.ZERO) > 0
                            ? h.getCurrentPrice()
                            : (asset.getCurrentPrice() != null && asset.getCurrentPrice().compareTo(BigDecimal.ZERO) > 0
                            ? asset.getCurrentPrice()
                            : h.getAverageBuyPrice());

                    BigDecimal invested = h.getTotalInvested() != null ? h.getTotalInvested() : BigDecimal.ZERO;
                    BigDecimal val = price.multiply(h.getQuantity());
                    BigDecimal unrealized = val.subtract(invested);

                    totalInvested = totalInvested.add(invested);
                    currentValue = currentValue.add(val);
                    totalUnrealizedPnL = totalUnrealizedPnL.add(unrealized);

                    HoldingAggregator agg = holdingAggMap.computeIfAbsent(asset.getId(), k -> new HoldingAggregator(asset));
                    agg.quantity = agg.quantity.add(h.getQuantity());
                    agg.totalInvested = agg.totalInvested.add(invested);
                    agg.currentValue = agg.currentValue.add(val);
                    agg.unrealizedPnL = agg.unrealizedPnL.add(unrealized);
                }
            }
        }

        // Build asset PnL breakdown list
        List<AssetPnLResponse> breakdown = new ArrayList<>();
        Set<Long> allAssetIds = new HashSet<>();
        allAssetIds.addAll(assetMap.keySet());

        for (Long assetId : allAssetIds) {
            Asset asset = assetMap.get(assetId);
            HoldingAggregator agg = holdingAggMap.get(assetId);

            BigDecimal qty = agg != null ? agg.quantity : BigDecimal.ZERO;
            BigDecimal invested = agg != null ? agg.totalInvested : BigDecimal.ZERO;
            BigDecimal val = agg != null ? agg.currentValue : BigDecimal.ZERO;
            BigDecimal unrealized = agg != null ? agg.unrealizedPnL : BigDecimal.ZERO;
            BigDecimal realized = assetRealizedMap.getOrDefault(assetId, BigDecimal.ZERO);
            BigDecimal totalAssetPnL = realized.add(unrealized);

            BigDecimal avgBuy = qty.compareTo(BigDecimal.ZERO) > 0
                    ? invested.divide(qty, 4, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            BigDecimal currPrice = asset.getCurrentPrice() != null ? asset.getCurrentPrice() : avgBuy;
            BigDecimal retPct = calculateReturnPercentage(invested, val, realized);

            breakdown.add(AssetPnLResponse.builder()
                    .symbol(asset.getSymbol())
                    .name(asset.getName())
                    .exchange(asset.getExchange())
                    .quantity(qty)
                    .averageBuyPrice(avgBuy)
                    .currentPrice(currPrice)
                    .realizedPnL(realized)
                    .unrealizedPnL(unrealized)
                    .totalPnL(totalAssetPnL)
                    .returnPercentage(retPct)
                    .build());
        }

        // Sort breakdown by total PnL descending
        breakdown.sort((a, b) -> b.getTotalPnL().compareTo(a.getTotalPnL()));

        BigDecimal overallPnL = totalRealizedPnL.add(totalUnrealizedPnL);
        BigDecimal overallReturnPercentage = calculateReturnPercentage(totalInvested, currentValue, totalRealizedPnL);

        return PnLResponse.builder()
                .totalRealizedPnL(totalRealizedPnL)
                .totalUnrealizedPnL(totalUnrealizedPnL)
                .dailyPnL(dailyPnL)
                .weeklyPnL(weeklyPnL)
                .monthlyPnL(monthlyPnL)
                .overallPnL(overallPnL)
                .overallReturnPercentage(overallReturnPercentage)
                .totalInvestment(totalInvested)
                .currentValue(currentValue)
                .assetPnLBreakdown(breakdown)
                .build();
    }

    private static class HoldingAggregator {
        Asset asset;
        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;
        BigDecimal unrealizedPnL = BigDecimal.ZERO;

        HoldingAggregator(Asset asset) {
            this.asset = asset;
        }
    }
}
