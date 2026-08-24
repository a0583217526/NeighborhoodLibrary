package com.library.smart_library_ai.service.clients;

import com.library.smart_library_ai.dto.BookDto;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Service
public class BookClient {
    private final WebClient webClient;

    public BookClient(WebClient.Builder builder, @Value("${service.books.url}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }

    public List<BookDto> getAllBooks() {
        return webClient.get()
                .uri("/api/books/allBooks")
                .retrieve()
                .bodyToFlux(BookDto.class)
                .collectList()
                .block(); // מחזיר את רשימת הספרים באופן סינכרוני
    }
}