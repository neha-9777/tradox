package com.tradox.service;

import com.tradox.dto.request.TransactionRequest;
import com.tradox.dto.response.TransactionResponse;
import com.tradox.entity.Asset;
import com.tradox.entity.Holding;
import com.tradox.entity.Portfolio;
import com.tradox.entity.Transaction;
import com.tradox.entity.Transaction.TransactionType;
import com.tradox.entity.User;
import com.tradox.exception.BadRequestException;
import com.tradox.exception.ResourceNotFoundException;
import com.tradox.repository.HoldingRepository;
import com.tradox.repository.PortfolioRepository;
import com.tradox.repository.TransactionRepository;
import com.tradox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;
    private final AssetService assetService;

    private final PnLEngineService pnlEngineService;

    public TransactionService(TransactionRepository transactionRepository,
                              PortfolioRepository portfolioRepository,
                              HoldingRepository holdingRepository,
                              UserRepository userRepository,
                              AssetService assetService,
                              PnLEngineService pnlEngineService) {
        this.transactionRepository = transactionRepository;
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.userRepository = userRepository;
        this.assetService = assetService;
        this.pnlEngineService = pnlEngineService;
    }

    @Transactional
    public TransactionResponse addTransaction(String userEmail, Long portfolioId, TransactionRequest request) {
        User user = getUserByEmail(userEmail);
        Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "id", portfolioId));

        Asset asset = assetService.getOrCreateAsset(
                request.getSymbol(),
                request.getAssetName(),
                request.getExchange(),
                request.getPrice()
        );

        BigDecimal quantity = request.getQuantity();
        BigDecimal price = request.getPrice();
        BigDecimal fee = request.getFee() != null ? request.getFee() : BigDecimal.ZERO;
        BigDecimal totalAmount = quantity.multiply(price).add(fee);

        LocalDateTime txDate = request.getTransactionDate() != null
                ? request.getTransactionDate()
                : LocalDateTime.now();

        Transaction transaction = Transaction.builder()
                .portfolio(portfolio)
                .asset(asset)
                .type(request.getType())
                .quantity(quantity)
                .price(price)
                .totalAmount(totalAmount)
                .fee(fee)
                .transactionDate(txDate)
                .notes(request.getNotes())
                .externalTxId(request.getExternalTxId())
                .build();

        Transaction savedTx = transactionRepository.save(transaction);

        // Update holding position & realized PnL
        recalculateHoldingPosition(portfolio, asset);

        return mapToResponse(transactionRepository.findById(savedTx.getId()).orElse(savedTx));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByPortfolio(String userEmail, Long portfolioId) {
        User user = getUserByEmail(userEmail);
        portfolioRepository.findByIdAndUserId(portfolioId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "id", portfolioId));

        return transactionRepository.findByPortfolioIdOrderByTransactionDateDesc(portfolioId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllUserTransactions(String userEmail) {
        User user = getUserByEmail(userEmail);
        return transactionRepository.findByPortfolioUserIdOrderByTransactionDateDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTransaction(String userEmail, Long portfolioId, Long transactionId) {
        User user = getUserByEmail(userEmail);
        Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "id", portfolioId));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        if (!transaction.getPortfolio().getId().equals(portfolio.getId())) {
            throw new BadRequestException("Transaction does not belong to the specified portfolio");
        }

        Asset asset = transaction.getAsset();
        transactionRepository.delete(transaction);

        // Recalculate position after deletion
        recalculateHoldingPosition(portfolio, asset);
    }

    private void recalculateHoldingPosition(Portfolio portfolio, Asset asset) {
        List<Transaction> transactions = transactionRepository
                .findByPortfolioIdAndAssetIdOrderByTransactionDateAsc(portfolio.getId(), asset.getId());

        BigDecimal currentQty = BigDecimal.ZERO;
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal avgPrice = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            if (tx.getType() == TransactionType.BUY) {
                BigDecimal buyAmount = tx.getQuantity().multiply(tx.getPrice());
                currentQty = currentQty.add(tx.getQuantity());
                totalInvested = totalInvested.add(buyAmount);
                if (currentQty.compareTo(BigDecimal.ZERO) > 0) {
                    avgPrice = totalInvested.divide(currentQty, 4, RoundingMode.HALF_UP);
                }
            } else if (tx.getType() == TransactionType.SELL) {
                if (tx.getQuantity().compareTo(currentQty) > 0) {
                    throw new BadRequestException("Cannot sell more quantity than currently held (" + currentQty + ")");
                }
                BigDecimal realizedPnL = pnlEngineService.calculateRealizedPnL(
                        tx.getQuantity(),
                        tx.getPrice(),
                        avgPrice,
                        tx.getFee()
                );
                tx.setRealizedPnL(realizedPnL);
                transactionRepository.save(tx);

                BigDecimal sellInvestedCost = tx.getQuantity().multiply(avgPrice);
                currentQty = currentQty.subtract(tx.getQuantity());
                totalInvested = totalInvested.subtract(sellInvestedCost);
                if (totalInvested.compareTo(BigDecimal.ZERO) < 0) {
                    totalInvested = BigDecimal.ZERO;
                }
            }
        }

        Optional<Holding> holdingOpt = holdingRepository.findByPortfolioIdAndAssetId(portfolio.getId(), asset.getId());

        if (currentQty.compareTo(BigDecimal.ZERO) <= 0) {
            holdingOpt.ifPresent(holdingRepository::delete);
        } else {
            Holding holding = holdingOpt.orElseGet(() -> Holding.builder()
                    .portfolio(portfolio)
                    .asset(asset)
                    .build());

            holding.setQuantity(currentQty);
            holding.setAverageBuyPrice(avgPrice);
            holding.setTotalInvested(totalInvested);
            holding.setCurrentPrice(asset.getCurrentPrice() != null ? asset.getCurrentPrice() : avgPrice);

            holdingRepository.save(holding);
        }
    }

    public TransactionResponse mapToResponse(Transaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .portfolioId(tx.getPortfolio().getId())
                .asset(assetService.mapToResponse(tx.getAsset()))
                .type(tx.getType())
                .quantity(tx.getQuantity())
                .price(tx.getPrice())
                .totalAmount(tx.getTotalAmount())
                .fee(tx.getFee())
                .transactionDate(tx.getTransactionDate())
                .notes(tx.getNotes())
                .realizedPnL(tx.getRealizedPnL())
                .externalTxId(tx.getExternalTxId())
                .build();
    }

    @Transactional(readOnly = true)
    public boolean existsByPortfolioAndExternalTxId(Long portfolioId, String externalTxId) {
        if (externalTxId == null || externalTxId.isBlank()) {
            return false;
        }
        return transactionRepository.existsByPortfolioIdAndExternalTxId(portfolioId, externalTxId);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
}
