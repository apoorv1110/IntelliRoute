package com.intelliroute.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIClient {

    private final WebClient webClient;

    @Value("${ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${ai.gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${ai.gemini.endpoint:https://generativelanguage.googleapis.com/v1beta}")
    private String geminiEndpoint;

    public double predictComplexity(String description) {
        if (StringUtils.hasText(geminiApiKey)) {
            Double score = predictWithGemini(description);
            if (score != null) {
                return score;
            }
            log.warn("Gemini call failed, falling back to heuristic scoring");
        }
        return heuristicComplexity(description);
    }

    private Double predictWithGemini(String description) {
        try {
            String prompt = """
                    You are scoring support queries for engineering triage. \
                    Return only a number between 1.0 (very simple) and 5.0 (very complex). \
                    No text, no units, just the number.

                    Query: %s
                    """.formatted(description);

            Map<String, Object> request = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))
                    )
            );

            Map<String, Object> response = webClient.post()
                    .uri(geminiEndpoint + "/models/" + geminiModel + ":generateContent?key=" + geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(request))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorResume(err -> {
                        log.warn("Gemini service call failed: {}", err.getMessage());
                        return Mono.empty();
                    })
                    .block();

            if (response == null || !response.containsKey("candidates")) {
                return null;
            }
            Object candidatesObj = response.get("candidates");
            if (!(candidatesObj instanceof List<?> candidates) || candidates.isEmpty()) {
                return null;
            }
            Object first = candidates.getFirst();
            if (!(first instanceof Map<?, ?> candidate)) {
                return null;
            }
            Object contentObj = candidate.get("content");
            if (!(contentObj instanceof Map<?, ?> content)) {
                return null;
            }
            Object partsObj = content.get("parts");
            if (!(partsObj instanceof List<?> parts) || parts.isEmpty()) {
                return null;
            }
            Object part = parts.getFirst();
            if (!(part instanceof Map<?, ?> partMap) || !partMap.containsKey("text")) {
                return null;
            }
            String text = partMap.get("text").toString().trim();
            // Gemini may return extra tokens; grab the first parsable number.
            String numeric = text.split("[^0-9\\.]")[0];
            return Double.parseDouble(numeric);
        } catch (Exception ex) {
            log.warn("Gemini parsing failed: {}", ex.getMessage());
            return null;
        }
    }

    private double heuristicComplexity(String description) {
        if (description == null || description.isBlank()) {
            return 2.5;
        }
        String text = description.toLowerCase();
        int length = text.length();

        double score = 1.0 + Math.min(length / 300.0, 3.0);

        if (text.contains("outage") || text.contains("critical")
                || text.contains("latency") || text.contains("security")
                || text.contains("data loss") || text.contains("p1")) {
            score += 1.2;
        }
        if (text.contains("architecture") || text.contains("refactor")) {
            score += 0.8;
        }
        if (text.contains("simple") || text.contains("typo")) {
            score -= 0.5;
        }

        score = Math.max(1.0, Math.min(score, 5.0));
        return Math.round(score * 100.0) / 100.0;
    }
}

