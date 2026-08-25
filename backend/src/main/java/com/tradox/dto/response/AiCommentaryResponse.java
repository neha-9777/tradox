package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCommentaryResponse {
    private String title;
    private String headline;
    private String narrative;
    private int healthScore;
    private String riskRating;
    private List<String> keyTakeaways;
    private List<String> actionableAdvice;
    private String modelUsed;
    private boolean isLiveLlm;
}
