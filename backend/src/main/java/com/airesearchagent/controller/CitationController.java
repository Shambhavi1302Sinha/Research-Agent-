package com.airesearchagent.controller;

import com.airesearchagent.dto.CitationDtos;
import com.airesearchagent.entity.User;
import com.airesearchagent.service.CitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/citations")
@RequiredArgsConstructor
public class CitationController {

    private final CitationService citationService;

    @PostMapping
    public CitationDtos.CitationResponse generate(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CitationDtos.CitationRequest request
    ) {
        return citationService.generateCitation(user, request);
    }
}
