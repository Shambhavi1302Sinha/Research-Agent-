package com.airesearchagent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

public class CitationDtos {

    public record CitationRequest(
            @NotBlank String title,
            @NotBlank String authors,
            @NotBlank String year,
            @NotBlank String publisher,
            @NotBlank String url,
            @NotBlank String style
    ) {}

    @Builder
    public record CitationResponse(
            Long id,
            String style,
            String generatedCitation,
            LocalDateTime createdAt
    ) {}
}
