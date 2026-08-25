package com.tradox.service;

import com.tradox.dto.request.LoginRequest;
import com.tradox.dto.request.RegisterRequest;
import com.tradox.dto.response.AuthResponse;
import com.tradox.entity.User;
import com.tradox.exception.BadRequestException;
import com.tradox.repository.UserRepository;
import com.tradox.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .email("trader@tradox.com")
                .password("encoded_password")
                .firstName("Jane")
                .lastName("Doe")
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void testRegister_Success() {
        RegisterRequest req = RegisterRequest.builder()
                .email("trader@tradox.com")
                .password("Password123!")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        when(userRepository.existsByEmail("trader@tradox.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenProvider.generateTokenFromEmail(anyString())).thenReturn("mock-jwt-token");

        AuthResponse resp = authService.register(req);

        assertNotNull(resp);
        assertEquals("trader@tradox.com", resp.getEmail());
        assertEquals("mock-jwt-token", resp.getToken());
    }

    @Test
    @DisplayName("Should throw BadRequestException when registering existing email")
    void testRegister_DuplicateEmail() {
        RegisterRequest req = RegisterRequest.builder()
                .email("trader@tradox.com")
                .password("Password123!")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        when(userRepository.existsByEmail("trader@tradox.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(req));
    }

    @Test
    @DisplayName("Should authenticate and return JWT token on login")
    void testLogin_Success() {
        LoginRequest req = LoginRequest.builder()
                .email("trader@tradox.com")
                .password("Password123!")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken("trader@tradox.com", "Password123!");
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(tokenProvider.generateToken(any())).thenReturn("mock-jwt-token");

        AuthResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("trader@tradox.com", resp.getEmail());
        assertEquals("mock-jwt-token", resp.getToken());
    }
}
