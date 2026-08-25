package com.tradox.repository;

import com.tradox.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findBySymbolIgnoreCaseAndExchangeIgnoreCase(String symbol, String exchange);

    Optional<Asset> findBySymbolIgnoreCase(String symbol);

    List<Asset> findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(String symbolQuery, String nameQuery);
}
