package com.library.smart_library_ai.service;

import com.library.smart_library_ai.dto.BookDto;
import com.library.smart_library_ai.service.clients.BookClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class BookCacheService {
    private final BookClient bookClient;
    private List<BookDto> cachedBooks;

    public BookCacheService(BookClient bookClient) {
        this.bookClient = bookClient;
    }

    // מתעדכן אוטומטית פעם בשעה
    @Scheduled(cron = "0 0 3 * * SUN")
    public void refreshBooks() {
        this.cachedBooks = bookClient.getAllBooks();
    }

    // טעינה ראשונית בעת עליית האפליקציה
    @PostConstruct
    public void init() {
        refreshBooks();
    }

    public List<BookDto> getBooks() {
        return cachedBooks;
    }
}