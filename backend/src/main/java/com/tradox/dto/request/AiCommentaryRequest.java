package com.tradox.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCommentaryRequest {
    private String apiKey;
    private String modelProvider;
    private String customPrompt;
}
