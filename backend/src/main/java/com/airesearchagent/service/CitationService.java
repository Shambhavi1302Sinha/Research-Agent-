package com.airesearchagent.service;

import com.airesearchagent.dto.CitationDtos;
import com.airesearchagent.entity.Citation;
import com.airesearchagent.entity.User;
import com.airesearchagent.repository.CitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CitationService {

    private final CitationRepository citationRepository;
    private final AiClientService aiClientService;

    public CitationDtos.CitationResponse generateCitation(User user, CitationDtos.CitationRequest request) {
        String prompt = """
                Generate a %s citation for this source.
                Title: %s
                Authors: %s
                Year: %s
                Publisher: %s
                URL: %s

                Return only the final citation text.
                """.formatted(
                request.style(),
                request.title(),
                request.authors(),
                request.year(),
                request.publisher(),
                request.url()
        );

        String generated = aiClientService.generateText(prompt).trim();
        Citation citation = citationRepository.save(Citation.builder()
                .user(user)
                .sourceTitle(request.title())
                .style(request.style())
                .generatedCitation(generated)
                .createdAt(LocalDateTime.now())
                .build());

        return CitationDtos.CitationResponse.builder()
                .id(citation.getId())
                .style(citation.getStyle())
                .generatedCitation(citation.getGeneratedCitation())
                .createdAt(citation.getCreatedAt())
                .build();
    }
}
