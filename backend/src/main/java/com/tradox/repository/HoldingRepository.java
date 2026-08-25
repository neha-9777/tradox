package com.tradox.repository;

import com.tradox.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    List<Holding> findByPortfolioIdOrderByTotalInvestedDesc(Long portfolioId);

    Optional<Holding> findByPortfolioIdAndAssetId(Long portfolioId, Long assetId);

    void deleteByPortfolioIdAndQuantityEquals(Long portfolioId, java.math.BigDecimal quantity);
}
