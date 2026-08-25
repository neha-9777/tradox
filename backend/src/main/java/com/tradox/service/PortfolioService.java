package com.tradox.service;

import com.tradox.dto.request.PortfolioRequest;
import com.tradox.dto.response.HoldingResponse;
import com.tradox.dto.response.PortfolioResponse;
import com.tradox.dto.response.PortfolioSummaryResponse;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;
    private final AssetService assetService;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            HoldingRepository holdingRepository,
                            UserRepository userRepository,
                            AssetService assetService) {
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.userRepository = userRepository;
        this.assetService = assetService;
    }

    @Transactional
    public PortfolioResponse createPortfolio(String userEmail, PortfolioRequest request) {
        User user = getUserByEmail(userEmail);

        List<Portfolio> userPortfolios = portfolioRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        boolean isFirst = userPortfolios.isEmpty();

        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .isDefault(isFirst || Boolean.TRUE.equals(request.getIsDefault()))
                .build();

        if (portfolio.getIsDefault() && !isFirst) {
            unsetOtherDefaults(user.getId());
        }

        Portfolio saved = portfolioRepository.save(portfolio);
        return mapToResponse(saved);
    }

    @Transactional
    public List<PortfolioResponse> getUserPortfolios(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Portfolio> portfolios = portfolioRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        // Auto-create default portfolio if user has none
        if (portfolios.isEmpty()) {
            Portfolio defaultPortfolio = portfolioRepository.save(Portfolio.builder()
                    .user(user)
                    .name("Main Portfolio")
                    .description("Default investment portfolio")
                    .currency("INR")
                    .isDefault(true)
                    .build());
            return List.of(mapToResponse(defaultPortfolio));
        }

        return portfolios.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolioById(String userEmail, Long portfolioId) {
        User user = getUserByEmail(userEmail);
        Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "id", portfolioId));
        return mapToResponse(portfolio);
    }

    @Transactional
    public PortfolioResponse updatePortfolio(String userEmail, Long portfolioId, PortfolioRequest request) {
        User user = getUserByEmail(userEmail);
        Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "id", portfolioId));

        portfolio.setName(request.getName());
        portfolio.setDescription(request.getDescription());
        if (request.getCurrency() != null) {
            portfolio.setCurrency(request.getCurrency());
        }

        if (Boolean.TRUE.equals(request.getIsDefault()) && !portfolio.getIsDefault()) {
            unsetOtherDefaults(user.getId());
            portfolio.setIsDefault(true);
        }

        Portfolio updated = portfolioRepository.save(portfolio);
        return mapToResponse(updated);
    }

    @Transactional
    public void deletePortfolio(String userEmail, Long portfolioId) {
        User user = getUserByEmail(userEmail);
        Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "id", portfolioId));
        portfolioRepository.delete(portfolio);
    }

    @Transactional(readOnly = true)
    public PortfolioSummaryResponse getOverallSummary(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Portfolio> portfolios = portfolioRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        BigDecimal totalInvestment = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;
        int activeHoldingsCount = 0;

        for (Portfolio p : portfolios) {
            List<Holding> holdings = holdingRepository.findByPortfolioIdOrderByTotalInvestedDesc(p.getId());
            for (Holding h : holdings) {
                if (h.getQuantity() != null && h.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    activeHoldingsCount++;
                    BigDecimal invested = h.getTotalInvested() != null ? h.getTotalInvested() : BigDecimal.ZERO;
                    BigDecimal price = h.getCurrentPrice() != null ? h.getCurrentPrice() : h.getAverageBuyPrice();
                    BigDecimal val = price.multiply(h.getQuantity());

                    totalInvestment = totalInvestment.add(invested);
                    currentValue = currentValue.add(val);
                }
            }
        }

        BigDecimal totalPnL = currentValue.subtract(totalInvestment);
        BigDecimal returnPercentage = BigDecimal.ZERO;
        if (totalInvestment.compareTo(BigDecimal.ZERO) > 0) {
            returnPercentage = totalPnL.divide(totalInvestment, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return PortfolioSummaryResponse.builder()
                .totalInvestment(totalInvestment)
                .currentValue(currentValue)
                .totalPnL(totalPnL)
                .returnPercentage(returnPercentage)
                .activeHoldingsCount(activeHoldingsCount)
                .totalPortfoliosCount(portfolios.size())
                .build();
    }

    public PortfolioResponse mapToResponse(Portfolio portfolio) {
        List<Holding> holdings = holdingRepository.findByPortfolioIdOrderByTotalInvestedDesc(portfolio.getId());

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        List<HoldingResponse> holdingResponses = holdings.stream()
                .filter(h -> h.getQuantity() != null && h.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                .map(h -> {
                    BigDecimal price = h.getCurrentPrice() != null && h.getCurrentPrice().compareTo(BigDecimal.ZERO) > 0
                            ? h.getCurrentPrice()
                            : (h.getAsset().getCurrentPrice() != null && h.getAsset().getCurrentPrice().compareTo(BigDecimal.ZERO) > 0
                            ? h.getAsset().getCurrentPrice()
                            : h.getAverageBuyPrice());

                    BigDecimal val = price.multiply(h.getQuantity());
                    BigDecimal pnl = val.subtract(h.getTotalInvested());
                    BigDecimal pnlPct = BigDecimal.ZERO;
                    if (h.getTotalInvested().compareTo(BigDecimal.ZERO) > 0) {
                        pnlPct = pnl.divide(h.getTotalInvested(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    }

                    h.setCurrentPrice(price);
                    h.setCurrentValue(val);
                    h.setUnrealizedPnL(pnl);
                    h.setUnrealizedPnLPercentage(pnlPct);

                    return HoldingResponse.builder()
                            .id(h.getId())
                            .portfolioId(portfolio.getId())
                            .asset(assetService.mapToResponse(h.getAsset()))
                            .quantity(h.getQuantity())
                            .averageBuyPrice(h.getAverageBuyPrice())
                            .totalInvested(h.getTotalInvested())
                            .currentPrice(price)
                            .currentValue(val)
                            .unrealizedPnL(pnl)
                            .unrealizedPnLPercentage(pnlPct)
                            .build();
                })
                .collect(Collectors.toList());

        for (HoldingResponse hr : holdingResponses) {
            totalInvested = totalInvested.add(hr.getTotalInvested());
            currentValue = currentValue.add(hr.getCurrentValue());
        }

        BigDecimal totalPnL = currentValue.subtract(totalInvested);
        BigDecimal returnPercentage = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            returnPercentage = totalPnL.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .currency(portfolio.getCurrency())
                .isDefault(portfolio.getIsDefault())
                .totalInvested(totalInvested)
                .currentValue(currentValue)
                .totalPnL(totalPnL)
                .returnPercentage(returnPercentage)
                .totalHoldingsCount(holdingResponses.size())
                .holdings(holdingResponses)
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private void unsetOtherDefaults(Long userId) {
        portfolioRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(p -> {
            p.setIsDefault(false);
            portfolioRepository.save(p);
        });
    }
}
