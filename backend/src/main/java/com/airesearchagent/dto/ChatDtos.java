package com.airesearchagent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ChatDtos {

    public record ChatRequest(@NotBlank String message) {}

    @Builder
    public record ChatMessageDto(
            String role,
            String message,
            LocalDateTime createdAt
    ) {}

    @Builder
    public record ChatResponse(
            ChatMessageDto reply,
            List<ChatMessageDto> history
    ) {}
}
