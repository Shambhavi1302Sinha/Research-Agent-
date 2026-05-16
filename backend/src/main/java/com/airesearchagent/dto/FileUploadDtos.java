package com.airesearchagent.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class FileUploadDtos {

    @Builder
    public record FileUploadResponse(
            Long id,
            String fileName,
            String summary,
            String extractedTextPreview,
            LocalDateTime createdAt
    ) {}
}
