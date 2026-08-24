package com.library.smart_library_ai.service.clients;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {GroqAiService.class, GroqAiIntegrationTest.TestConfig.class})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class GroqAiIntegrationTest {

    @Autowired
    private GroqAiService groqAiService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }

    @Test
    void testRealAiConnectionAndOutput() {
        String prompt = "תן לי 3 מספרי ספרים (IDs) בתחום המדע.";

        // הקריאה משתמשת בערכים מתוך application.properties הראשי
        String result = groqAiService.generateAsync(prompt);

        assertNotNull(result, "התשובה מה-AI לא אמורה להיות null");
        assertFalse(result.isEmpty(), "התשובה מה-AI לא אמורה להיות ריקה");
    }
}