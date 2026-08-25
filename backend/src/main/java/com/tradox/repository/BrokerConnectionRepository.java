package com.tradox.repository;

import com.tradox.entity.BrokerConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrokerConnectionRepository extends JpaRepository<BrokerConnection, Long> {

    List<BrokerConnection> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<BrokerConnection> findByIdAndUserId(Long id, Long userId);

    Optional<BrokerConnection> findByUserIdAndBrokerId(Long userId, String brokerId);
}
