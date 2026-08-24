package com.library.smart_library_ai.service.clients;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroqAiServiceTest {

    private MockWebServer mockWebServer;
    private GroqAiService groqAiService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // כתובת השרת המדומה
        String baseUrl = mockWebServer.url("/").toString();

        // יצירת ה-Service עם פרמטרים מדומה (לצורך הטסט בלבד)
        // את ה-baseUrl אנחנו מזריקים לתוך ה-Builder,
        // וה-GroqAiService ישתמש בזה במקום בכתובת האמיתית.
        WebClient.Builder builder = WebClient.builder();

        // כאן אנחנו מזריקים ערכים פיקטיביים כדי שהקונסטרקטור לא ייכשל
        groqAiService = new GroqAiService(builder, baseUrl, "fake-api-key", "fake-model");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGenerateAsync_ReturnsContent() {
        // 1. הכנה: תגובה מדומה
        String mockJsonResponse = """
            {
                "choices": [{
                    "message": { "content": "תשובה מדומה מה-AI" }
                }]
            }
            """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockJsonResponse)
                .addHeader("Content-Type", "application/json"));

        // 2. הרצה
        String result = groqAiService.generateAsync("המלץ לי על ספרים");

        // 3. בדיקה
        assertEquals("תשובה מדומה מה-AI", result);
    }
}