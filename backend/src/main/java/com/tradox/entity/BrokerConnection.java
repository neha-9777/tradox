package com.tradox.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "broker_connections", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "broker_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrokerConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "broker_id", nullable = false, length = 50)
    private String brokerId;

    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ConnectionStatus status = ConnectionStatus.CONNECTED;

    @Column(name = "api_key", length = 255)
    private String apiKey;

    @Column(name = "access_token", length = 255)
    private String accessToken;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum ConnectionStatus {
        CONNECTED, DISCONNECTED, ERROR
    }
}
