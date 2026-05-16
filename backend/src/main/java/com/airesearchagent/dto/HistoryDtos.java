package com.airesearchagent.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class HistoryDtos {

    @Builder
    public record HistoryItem(
            Long id,
            String topic,
            String summary,
            LocalDateTime createdAt
    ) {}
}
