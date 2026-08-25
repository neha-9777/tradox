package com.tradox.service;

import com.tradox.dto.response.AuditLogResponse;
import com.tradox.entity.AuditLog;
import com.tradox.entity.User;
import com.tradox.repository.AuditLogRepository;
import com.tradox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityAndReliabilityTest {

    private EncryptionService encryptionService;
    private RateLimiterService rateLimiterService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
        rateLimiterService = new RateLimiterService();

        sampleUser = User.builder()
                .id(1L)
                .email("trader@tradox.com")
                .firstName("Jane")
                .lastName("Doe")
                .build();
    }

    @Test
    @DisplayName("Should correctly encrypt and decrypt secrets using AES-256")
    void testEncryptionAndDecryption() {
        String plainSecret = "kite_api_secret_99882233";

        String cipherText = encryptionService.encrypt(plainSecret);

        assertNotNull(cipherText);
        assertNotEquals(plainSecret, cipherText);

        String decrypted = encryptionService.decrypt(cipherText);
        assertEquals(plainSecret, decrypted);
    }

    @Test
    @DisplayName("Should enforce sliding window rate limiting limits")
    void testRateLimiterService() {
        String clientIp = "192.168.1.100";

        // First 60 requests should pass
        for (int i = 0; i < 60; i++) {
            assertTrue(rateLimiterService.allowRequest(clientIp));
        }

        // 61st request should be throttled
        assertFalse(rateLimiterService.allowRequest(clientIp));
        assertEquals(0, rateLimiterService.getRemainingRequests(clientIp));
    }

    @Test
    @DisplayName("Should successfully record and retrieve security audit logs")
    void testAuditLogService() {
        AuditLog sampleLog = AuditLog.builder()
                .id(10L)
                .user(sampleUser)
                .actionType("USER_LOGIN")
                .resourceName("Auth")
                .ipAddress("127.0.0.1")
                .status("SUCCESS")
                .details("User authenticated")
                .timestamp(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(auditLogRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(List.of(sampleLog));

        List<AuditLogResponse> logs = auditLogService.getUserAuditLogs("trader@tradox.com");

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals("USER_LOGIN", logs.get(0).getActionType());
        assertEquals("SUCCESS", logs.get(0).getStatus());
    }
}
