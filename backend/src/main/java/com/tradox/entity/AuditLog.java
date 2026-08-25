package com.tradox.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // USER_LOGIN, PORTFOLIO_CREATED, TRANSACTION_ADD, BROKER_CONNECTED, BROKER_SYNC

    @Column(name = "resource_name", length = 100)
    private String resourceName;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(nullable = false, length = 20)
    private String status; // SUCCESS, FAILED, WARN

    @Column(length = 500)
    private String details;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
