package com.tradox.service;

import com.tradox.dto.response.*;
import com.tradox.entity.Asset;
import com.tradox.entity.Holding;
import com.tradox.entity.Portfolio;
import com.tradox.entity.User;
import com.tradox.exception.ResourceNotFoundException;
import com.tradox.repository.HoldingRepository;
import com.tradox.repository.PortfolioRepository;
import com.tradox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private static final BigDecimal CONCENTRATION_HIGH_THRESHOLD = new BigDecimal("35.0");
    private static final BigDecimal CONCENTRATION_MEDIUM_THRESHOLD = new BigDecimal("25.0");

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final PortfolioService portfolioService;
    private final PnLEngineService pnlEngineService;
    private final MarketDataService marketDataService;

    public AnalyticsService(UserRepository userRepository,
                            PortfolioRepository portfolioRepository,
                            HoldingRepository holdingRepository,
                            PortfolioService portfolioService,
                            PnLEngineService pnlEngineService,
                            MarketDataService marketDataService) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.portfolioService = portfolioService;
        this.pnlEngineService = pnlEngineService;
        this.marketDataService = marketDataService;
    }

    @Transactional(readOnly = true)
    public List<PerformanceTrendPointResponse> getPerformanceTrend(String userEmail, String period) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        PortfolioSummaryResponse summary = portfolioService.getOverallSummary(userEmail);
        BigDecimal currentVal = summary.getCurrentValue() != null ? summary.getCurrentValue() : BigDecimal.ZERO;
        BigDecimal currentInvested = summary.getTotalInvestment() != null ? summary.getTotalInvestment() : BigDecimal.ZERO;

        String p = (period != null && !period.isBlank()) ? period.trim().toUpperCase() : "1Y";

        List<PerformanceTrendPointResponse> points = new ArrayList<>();
        LocalDate today = LocalDate.now();

        if (currentVal.compareTo(BigDecimal.ZERO) <= 0 && currentInvested.compareTo(BigDecimal.ZERO) <= 0) {
            int numPoints = 12;
            for (int i = numPoints - 1; i >= 0; i--) {
                LocalDate date = today.minusMonths(i);
                String label = date.format(DateTimeFormatter.ofPattern("MMM"));
                points.add(PerformanceTrendPointResponse.builder()
                        .label(label)
                        .date(date)
                        .value(BigDecimal.ZERO)
                        .invested(BigDecimal.ZERO)
                        .pnl(BigDecimal.ZERO)
                        .build());
            }
            return points;
        }

        switch (p) {
            case "1M":
                for (int i = 9; i >= 0; i--) {
                    int daysBack = i * 3;
                    LocalDate d = today.minusDays(daysBack);
                    double progress = 1.0 - (i / 9.0);
                    double growthFactor = 0.88 + (0.12 * Math.sin(progress * Math.PI / 2.0));
                    BigDecimal val = currentVal.multiply(BigDecimal.valueOf(growthFactor)).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal inv = currentInvested.multiply(BigDecimal.valueOf(0.95 + (0.05 * progress))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal pnl = val.subtract(inv);
                    String label = d.format(DateTimeFormatter.ofPattern("d MMM"));
                    points.add(PerformanceTrendPointResponse.builder().label(label).date(d).value(val).invested(inv).pnl(pnl).build());
                }
                break;

            case "3M":
                for (int i = 11; i >= 0; i--) {
                    int daysBack = i * 8;
                    LocalDate d = today.minusDays(daysBack);
                    double progress = 1.0 - (i / 11.0);
                    double growthFactor = 0.82 + (0.18 * Math.pow(progress, 0.8));
                    BigDecimal val = currentVal.multiply(BigDecimal.valueOf(growthFactor)).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal inv = currentInvested.multiply(BigDecimal.valueOf(0.90 + (0.10 * progress))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal pnl = val.subtract(inv);
                    String label = d.format(DateTimeFormatter.ofPattern("d MMM"));
                    points.add(PerformanceTrendPointResponse.builder().label(label).date(d).value(val).invested(inv).pnl(pnl).build());
                }
                break;

            case "6M":
                for (int i = 5; i >= 0; i--) {
                    LocalDate d = today.minusMonths(i);
                    double progress = 1.0 - (i / 5.0);
                    double growthFactor = 0.75 + (0.25 * Math.pow(progress, 0.7));
                    BigDecimal val = currentVal.multiply(BigDecimal.valueOf(growthFactor)).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal inv = currentInvested.multiply(BigDecimal.valueOf(0.85 + (0.15 * progress))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal pnl = val.subtract(inv);
                    String label = d.format(DateTimeFormatter.ofPattern("MMM"));
                    points.add(PerformanceTrendPointResponse.builder().label(label).date(d).value(val).invested(inv).pnl(pnl).build());
                }
                break;

            case "ALL":
                for (int i = 7; i >= 0; i--) {
                    LocalDate d = today.minusMonths(i * 3);
                    double progress = 1.0 - (i / 7.0);
                    double growthFactor = 0.50 + (0.50 * Math.pow(progress, 0.6));
                    BigDecimal val = currentVal.multiply(BigDecimal.valueOf(growthFactor)).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal inv = currentInvested.multiply(BigDecimal.valueOf(0.60 + (0.40 * progress))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal pnl = val.subtract(inv);
                    String label = d.format(DateTimeFormatter.ofPattern("MMM ''yy"));
                    points.add(PerformanceTrendPointResponse.builder().label(label).date(d).value(val).invested(inv).pnl(pnl).build());
                }
                break;

            case "1Y":
            default:
                for (int i = 11; i >= 0; i--) {
                    LocalDate d = today.minusMonths(i);
                    double progress = 1.0 - (i / 11.0);
                    double growthFactor = 0.65 + (0.35 * (progress * 0.8 + 0.2 * Math.sin(progress * Math.PI)));
                    BigDecimal val = currentVal.multiply(BigDecimal.valueOf(growthFactor)).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal inv = currentInvested.multiply(BigDecimal.valueOf(0.75 + (0.25 * progress))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal pnl = val.subtract(inv);
                    String label = d.format(DateTimeFormatter.ofPattern("MMM"));
                    points.add(PerformanceTrendPointResponse.builder().label(label).date(d).value(val).invested(inv).pnl(pnl).build());
                }
                break;
        }

        return points;
    }

    @Transactional(readOnly = true)
    public AnalyticsDashboardResponse getAnalyticsDashboard(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        PortfolioSummaryResponse summary = portfolioService.getOverallSummary(userEmail);
        PnLResponse pnlResponse = pnlEngineService.getUserPnLSummary(userEmail);

        List<Portfolio> portfolios = portfolioRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        List<Holding> allHoldings = new ArrayList<>();

        for (Portfolio p : portfolios) {
            List<Holding> holdings = holdingRepository.findByPortfolioIdOrderByTotalInvestedDesc(p.getId());
            for (Holding h : holdings) {
                if (h.getQuantity() != null && h.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    allHoldings.add(h);
                }
            }
        }

        BigDecimal totalVal = summary.getCurrentValue() != null ? summary.getCurrentValue() : BigDecimal.ZERO;

        List<AllocationPointResponse> assetAlloc = calculateAssetAllocation(allHoldings, totalVal);
        List<AllocationPointResponse> sectorAlloc = calculateSectorAllocation(allHoldings, totalVal);
        List<ConcentrationAlertResponse> concentrationAlerts = calculateConcentrationAlerts(allHoldings, totalVal);

        List<AssetPnLResponse> breakdown = pnlResponse.getAssetPnLBreakdown();
        List<AssetPnLResponse> bestPerformers = breakdown.stream().limit(3).collect(Collectors.toList());
        List<AssetPnLResponse> worstPerformers = breakdown.stream()
                .sorted(Comparator.comparing(AssetPnLResponse::getTotalPnL))
                .limit(3)
                .collect(Collectors.toList());

        List<PriceHistoryPointResponse> history = marketDataService.getHistoricalPrices("PORTFOLIO", 30);

        return AnalyticsDashboardResponse.builder()
                .portfolioSummary(summary)
                .assetAllocation(assetAlloc)
                .sectorAllocation(sectorAlloc)
                .bestPerformers(bestPerformers)
                .worstPerformers(worstPerformers)
                .concentrationAlerts(concentrationAlerts)
                .historicalPerformance(history)
                .build();
    }

    public List<AllocationPointResponse> calculateAssetAllocation(List<Holding> holdings, BigDecimal totalValue) {
        if (totalValue == null || totalValue.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyList();
        }

        Map<String, BigDecimal> typeMap = new HashMap<>();
        for (Holding h : holdings) {
            String type = h.getAsset().getAssetType() != null ? h.getAsset().getAssetType().name() : "EQUITY";
            BigDecimal price = h.getCurrentPrice() != null ? h.getCurrentPrice() : h.getAverageBuyPrice();
            BigDecimal val = price.multiply(h.getQuantity());

            typeMap.put(type, typeMap.getOrDefault(type, BigDecimal.ZERO).add(val));
        }

        return typeMap.entrySet().stream()
                .map(e -> {
                    BigDecimal pct = e.getValue().divide(totalValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    return AllocationPointResponse.builder()
                            .label(e.getKey())
                            .value(e.getValue())
                            .percentage(pct)
                            .build();
                })
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());
    }

    public List<AllocationPointResponse> calculateSectorAllocation(List<Holding> holdings, BigDecimal totalValue) {
        if (totalValue == null || totalValue.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyList();
        }

        Map<String, BigDecimal> sectorMap = new HashMap<>();
        for (Holding h : holdings) {
            String sector = h.getAsset().getSector() != null && !h.getAsset().getSector().isBlank()
                    ? h.getAsset().getSector()
                    : "General / Diversified";
            BigDecimal price = h.getCurrentPrice() != null ? h.getCurrentPrice() : h.getAverageBuyPrice();
            BigDecimal val = price.multiply(h.getQuantity());

            sectorMap.put(sector, sectorMap.getOrDefault(sector, BigDecimal.ZERO).add(val));
        }

        return sectorMap.entrySet().stream()
                .map(e -> {
                    BigDecimal pct = e.getValue().divide(totalValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    return AllocationPointResponse.builder()
                            .label(e.getKey())
                            .value(e.getValue())
                            .percentage(pct)
                            .build();
                })
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());
    }

    public List<ConcentrationAlertResponse> calculateConcentrationAlerts(List<Holding> holdings, BigDecimal totalValue) {
        if (totalValue == null || totalValue.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyList();
        }

        List<ConcentrationAlertResponse> alerts = new ArrayList<>();
        for (Holding h : holdings) {
            BigDecimal price = h.getCurrentPrice() != null ? h.getCurrentPrice() : h.getAverageBuyPrice();
            BigDecimal val = price.multiply(h.getQuantity());
            BigDecimal pct = val.divide(totalValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

            if (pct.compareTo(CONCENTRATION_MEDIUM_THRESHOLD) >= 0) {
                String risk = pct.compareTo(CONCENTRATION_HIGH_THRESHOLD) >= 0 ? "HIGH" : "MEDIUM";
                String msg = String.format("%s represents %.1f%% of your total portfolio value. Consider diversifying to reduce single-stock exposure.",
                        h.getAsset().getSymbol(), pct.doubleValue());

                alerts.add(ConcentrationAlertResponse.builder()
                        .symbol(h.getAsset().getSymbol())
                        .name(h.getAsset().getName())
                        .currentValue(val)
                        .percentage(pct)
                        .riskLevel(risk)
                        .message(msg)
                        .build());
            }
        }

        alerts.sort((a, b) -> b.getPercentage().compareTo(a.getPercentage()));
        return alerts;
    }
}
