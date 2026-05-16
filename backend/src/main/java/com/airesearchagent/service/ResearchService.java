package com.airesearchagent.service;

import com.airesearchagent.dto.HistoryDtos;
import com.airesearchagent.dto.ResearchDtos;
import com.airesearchagent.entity.ResearchHistory;
import com.airesearchagent.entity.User;
import com.airesearchagent.repository.ResearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResearchService {

    private final AiClientService aiClientService;
    private final ResearchHistoryRepository researchHistoryRepository;

    public ResearchDtos.ResearchResponse researchTopic(User user, ResearchDtos.ResearchRequest request) {
        String prompt = """
                You are an AI research assistant.
                Research the topic: %s

                Return plain text using this exact structure:
                SUMMARY:
                <short paragraph>

                KEY_INSIGHTS:
                - insight 1
                - insight 2
                - insight 3

                IMPORTANT_POINTS:
                - point 1
                - point 2
                - point 3

                RELATED_TOPICS:
                - related topic 1
                - related topic 2
                - related topic 3
                """.formatted(request.topic());

        String aiText = aiClientService.generateText(prompt);
        ParsedResearch parsed = parseResearch(aiText);

        ResearchHistory saved = researchHistoryRepository.save(
                ResearchHistory.builder()
                        .user(user)
                        .topic(request.topic())
                        .summary(parsed.summary())
                        .keyInsights(String.join("\n", parsed.keyInsights()))
                        .importantPoints(String.join("\n", parsed.importantPoints()))
                        .relatedTopics(String.join("\n", parsed.relatedTopics()))
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return ResearchDtos.ResearchResponse.builder()
                .id(saved.getId())
                .topic(saved.getTopic())
                .summary(parsed.summary())
                .keyInsights(parsed.keyInsights())
                .importantPoints(parsed.importantPoints())
                .relatedTopics(parsed.relatedTopics())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public List<HistoryDtos.HistoryItem> history(User user) {
        return researchHistoryRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(item -> HistoryDtos.HistoryItem.builder()
                        .id(item.getId())
                        .topic(item.getTopic())
                        .summary(item.getSummary())
                        .createdAt(item.getCreatedAt())
                        .build())
                .toList();
    }

    private ParsedResearch parseResearch(String value) {
        String summary = extractSection(value, "SUMMARY:", "KEY_INSIGHTS:");
        List<String> insights = extractList(extractSection(value, "KEY_INSIGHTS:", "IMPORTANT_POINTS:"));
        List<String> points = extractList(extractSection(value, "IMPORTANT_POINTS:", "RELATED_TOPICS:"));
        List<String> related = extractList(extractSection(value, "RELATED_TOPICS:", null));
        return new ParsedResearch(summary, insights, points, related);
    }

    private String extractSection(String raw, String start, String end) {
        int startIndex = raw.indexOf(start);
        if (startIndex < 0) {
            return "";
        }
        int contentStart = startIndex + start.length();
        int endIndex = end == null ? raw.length() : raw.indexOf(end, contentStart);
        if (endIndex < 0) {
            endIndex = raw.length();
        }
        return raw.substring(contentStart, endIndex).trim();
    }

    private List<String> extractList(String block) {
        return Arrays.stream(block.split("\\R"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .map(line -> line.replaceFirst("^-\\s*", ""))
                .collect(Collectors.toList());
    }

    private record ParsedResearch(
            String summary,
            List<String> keyInsights,
            List<String> importantPoints,
            List<String> relatedTopics
    ) {}
}
