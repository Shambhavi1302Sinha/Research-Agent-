package com.airesearchagent.controller;

import com.airesearchagent.dto.HistoryDtos;
import com.airesearchagent.dto.ResearchDtos;
import com.airesearchagent.entity.User;
import com.airesearchagent.service.ResearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/research")
@RequiredArgsConstructor
public class ResearchController {

    private final ResearchService researchService;

    @PostMapping
    public ResearchDtos.ResearchResponse research(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ResearchDtos.ResearchRequest request
    ) {
        return researchService.researchTopic(user, request);
    }

    @GetMapping("/history")
    public List<HistoryDtos.HistoryItem> history(@AuthenticationPrincipal User user) {
        return researchService.history(user);
    }
}
