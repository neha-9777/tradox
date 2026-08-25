package com.tradox.repository;

import com.tradox.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByPortfolioIdOrderByTransactionDateDesc(Long portfolioId);

    List<Transaction> findByPortfolioIdAndAssetIdOrderByTransactionDateAsc(Long portfolioId, Long assetId);

    List<Transaction> findByPortfolioUserIdOrderByTransactionDateDesc(Long userId);

    boolean existsByPortfolioIdAndExternalTxId(Long portfolioId, String externalTxId);
}
