package com.tradox.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "broker_sync_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrokerSyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "broker_connection_id", nullable = false)
    private BrokerConnection brokerConnection;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SyncStatus status;

    @Column(name = "holdings_synced")
    @Builder.Default
    private Integer holdingsSynced = 0;

    @Column(name = "transactions_synced")
    @Builder.Default
    private Integer transactionsSynced = 0;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "sync_started_at", nullable = false)
    private LocalDateTime syncStartedAt;

    @Column(name = "sync_completed_at")
    private LocalDateTime syncCompletedAt;

    public enum SyncStatus {
        IN_PROGRESS, SUCCESS, FAILED
    }
}
