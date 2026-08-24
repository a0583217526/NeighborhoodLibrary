package com.library.smart_library_ai.service.clients;

import com.library.smart_library_ai.dto.LoanHistoryDto;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Collections;
import reactor.core.publisher.Mono;
@Service
public class LoanClient {
    private final WebClient webClient;

    public LoanClient(WebClient.Builder builder, @Value("${service.loans.url}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }

    public List<LoanHistoryDto> getLoanHistory(int userId) {
        return webClient.get()
                .uri("/api/loans/user/{id}", userId)
                .retrieve()
                .bodyToFlux(LoanHistoryDto.class)
                .collectList()
                .block();
    }
}