package com.tradox.repository;

import com.tradox.entity.BrokerSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrokerSyncLogRepository extends JpaRepository<BrokerSyncLog, Long> {

    List<BrokerSyncLog> findByBrokerConnectionIdOrderBySyncStartedAtDesc(Long brokerConnectionId);

    List<BrokerSyncLog> findByBrokerConnectionUserIdOrderBySyncStartedAtDesc(Long userId);
}
