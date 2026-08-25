package com.tradox.service;

import com.tradox.dto.response.AssetResponse;
import com.tradox.entity.Asset;
import com.tradox.entity.Asset.AssetType;
import com.tradox.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Transactional
    public Asset getOrCreateAsset(String symbol, String name, String exchange, BigDecimal currentPrice) {
        String cleanSymbol = symbol.trim().toUpperCase();
        String cleanExchange = (exchange != null && !exchange.isBlank()) ? exchange.trim().toUpperCase() : "NSE";

        return assetRepository.findBySymbolIgnoreCaseAndExchangeIgnoreCase(cleanSymbol, cleanExchange)
                .map(existing -> {
                    if (currentPrice != null && currentPrice.compareTo(BigDecimal.ZERO) > 0) {
                        existing.setCurrentPrice(currentPrice);
                        return assetRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    Asset newAsset = Asset.builder()
                            .symbol(cleanSymbol)
                            .name((name != null && !name.isBlank()) ? name.trim() : cleanSymbol)
                            .exchange(cleanExchange)
                            .assetType(AssetType.EQUITY)
                            .currency("INR")
                            .currentPrice(currentPrice != null ? currentPrice : BigDecimal.ZERO)
                            .build();
                    return assetRepository.save(newAsset);
                });
    }

    @Transactional(readOnly = true)
    public List<AssetResponse> searchAssets(String query) {
        return assetRepository.findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(query, query)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AssetResponse mapToResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .symbol(asset.getSymbol())
                .name(asset.getName())
                .assetType(asset.getAssetType())
                .exchange(asset.getExchange())
                .sector(asset.getSector())
                .currency(asset.getCurrency())
                .currentPrice(asset.getCurrentPrice())
                .build();
    }
}
