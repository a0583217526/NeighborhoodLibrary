package com.library.smart_library_ai.service.clients;

import com.library.smart_library_ai.dto.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Collections;
import reactor.core.publisher.Mono;

@Service
public class UserClient {
    private final WebClient webClient;

    public UserClient(WebClient.Builder builder, @Value("${service.users.url}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }

    public List<UserDto> getAllUsers() {
        return webClient.get()
                .uri("/api/users")
                .retrieve()
                .bodyToFlux(UserDto.class)
                .collectList()
                .block();
    }
    public UserDto getUserById(int userId) {
        try {
            return webClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    // אם השרת מחזיר שגיאה (כמו 404), זה יזרוק Exception
                    .bodyToMono(UserDto.class)
                    .block();
        } catch (Exception e) {
            // טיפול במידה והשירות השני לא זמין או שהמשתמש לא נמצא
            throw new RuntimeException("Failed to fetch user from User Service: " + e.getMessage());
        }
    }
}