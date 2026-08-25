package com.tradox.service;

import com.tradox.dto.request.PortfolioRequest;
import com.tradox.dto.response.PortfolioResponse;
import com.tradox.dto.response.PortfolioSummaryResponse;
import com.tradox.entity.Portfolio;
import com.tradox.entity.User;
import com.tradox.repository.HoldingRepository;
import com.tradox.repository.PortfolioRepository;
import com.tradox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HoldingRepository holdingRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private User sampleUser;
    private Portfolio defaultPortfolio;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .email("trader@tradox.com")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        defaultPortfolio = Portfolio.builder()
                .id(10L)
                .user(sampleUser)
                .name("Main Portfolio")
                .description("Default main portfolio")
                .isDefault(true)
                .build();
    }

    @Test
    @DisplayName("Should create a custom user portfolio")
    void testCreatePortfolio() {
        PortfolioRequest req = PortfolioRequest.builder()
                .name("Tech Growth")
                .description("High risk tech stocks")
                .isDefault(false)
                .build();

        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(portfolioRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(defaultPortfolio));
        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(inv -> {
            Portfolio p = inv.getArgument(0);
            p.setId(11L);
            return p;
        });

        PortfolioResponse resp = portfolioService.createPortfolio("trader@tradox.com", req);

        assertNotNull(resp);
        assertEquals("Tech Growth", resp.getName());
        assertFalse(resp.getIsDefault());
    }

    @Test
    @DisplayName("Should return user portfolios and auto-create default if none exist")
    void testGetUserPortfolios_AutoCreateDefault() {
        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(portfolioRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(inv -> {
            Portfolio p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        List<PortfolioResponse> list = portfolioService.getUserPortfolios("trader@tradox.com");

        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0).getIsDefault());
    }

    @Test
    @DisplayName("Should calculate overall portfolio summary metrics correctly")
    void testGetOverallSummary() {
        when(userRepository.findByEmail("trader@tradox.com")).thenReturn(Optional.of(sampleUser));
        when(portfolioRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(defaultPortfolio));
        when(holdingRepository.findByPortfolioIdOrderByTotalInvestedDesc(10L)).thenReturn(List.of());

        PortfolioSummaryResponse summary = portfolioService.getOverallSummary("trader@tradox.com");

        assertNotNull(summary);
        assertEquals(0, summary.getTotalInvestment().compareTo(BigDecimal.ZERO));
        assertEquals(0, summary.getCurrentValue().compareTo(BigDecimal.ZERO));
        assertEquals(1, summary.getTotalPortfoliosCount());
    }
}
