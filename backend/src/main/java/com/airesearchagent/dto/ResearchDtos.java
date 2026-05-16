package com.airesearchagent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ResearchDtos {

    public record ResearchRequest(@NotBlank String topic) {}

    @Builder
    public record ResearchResponse(
            Long id,
            String topic,
            String summary,
            List<String> keyInsights,
            List<String> importantPoints,
            List<String> relatedTopics,
            LocalDateTime createdAt
    ) {}
}
