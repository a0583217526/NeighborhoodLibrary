package com.library.smart_library_ai.service.clients;

import com.library.smart_library_ai.dto.BookDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookClientTest {

    private static MockWebServer mockWebServer;
    private BookClient bookClient;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        // מחברים את ה-Client לכתובת של השרת המדומה
        String baseUrl = mockWebServer.url("/").toString();
        bookClient = new BookClient(WebClient.builder(), baseUrl);
    }

    @Test
    void testGetAllBooks_Success() {
        // הכנת תשובה מדומה מהשרת
        String jsonResponse = "[{\"bookId\":1, \"title\":\"Java Basics\"}, {\"bookId\":2, \"title\":\"Spring Guide\"}]";
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        // הרצה
        List<BookDto> books = bookClient.getAllBooks();

        // בדיקה
        assertNotNull(books);
        assertEquals(2, books.size());
        assertEquals("Java Basics", books.get(0).getTitle());
    }
}