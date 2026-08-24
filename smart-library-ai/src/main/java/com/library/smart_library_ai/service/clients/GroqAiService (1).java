package com.library.smart_library_ai.service.clients;

import com.library.smart_library_ai.dto.GroqResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class GroqAiService {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    // הזרקת הערכים מקובץ ה-properties
    public GroqAiService(
            WebClient.Builder webClientBuilder,
            @Value("${groq.api.url}") String baseUrl,
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.model}") String model) {

        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.model = model;
    }

    public String generateAsync(String userPrompt) {
        Map<String, Object> payload = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role", "user", "content", userPrompt)
                }
        );

        GroqResponse response = webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(GroqResponse.class)
                .block();

        return (response != null && !response.choices().isEmpty())
                ? response.choices().get(0).message().content()
                : "";
    }
}