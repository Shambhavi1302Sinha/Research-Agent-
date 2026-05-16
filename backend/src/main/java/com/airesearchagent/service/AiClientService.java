package com.airesearchagent.service;

import com.airesearchagent.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiClientService {

    private final WebClient webClient;

    @Value("${app.ai.provider:gemini}")
    private String provider;

    @Value("${app.ai.gemini-url}")
    private String geminiUrl;

    @Value("${GEMINI_API_KEY:}")
    private String geminiApiKey;

    public String generateText(String prompt) {
        if (!"gemini".equalsIgnoreCase(provider)) {
            throw new ApiException("Configured AI provider is not supported in this scaffold.");
        }
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new ApiException("Missing GEMINI_API_KEY environment variable.");
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        Map<?, ?> response;
        try {
            response = webClient.post()
                    .uri(geminiUrl + "?key=" + geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException exception) {
            throw new ApiException("Gemini API error " + exception.getStatusCode().value()
                    + ": check GEMINI_URL/model name and API key.");
        }

        try {
            List<?> candidates = (List<?>) response.get("candidates");
            Map<?, ?> firstCandidate = (Map<?, ?>) candidates.getFirst();
            Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> firstPart = (Map<?, ?>) parts.getFirst();
            return String.valueOf(firstPart.get("text"));
        } catch (Exception exception) {
            throw new ApiException("AI response could not be parsed.");
        }
    }
}
