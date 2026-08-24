package com.library.smart_library_ai.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.library.smart_library_ai.dto.BookDto;
import com.library.smart_library_ai.dto.LoanHistoryDto;
import com.library.smart_library_ai.dto.BookRecommendationDto;
import com.library.smart_library_ai.dto.UserDto;
import com.library.smart_library_ai.entity.BookRecommendation;
import com.library.smart_library_ai.repository.BookRecommendationRepository;
import com.library.smart_library_ai.service.clients.GroqAiService;
import com.library.smart_library_ai.service.clients.LoanClient;
import com.library.smart_library_ai.service.clients.UserClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookRecommendationServiceTest {

    @Mock private BookRecommendationRepository repository;
    @Mock private UserClient userClient;
    @Mock private LoanClient loanClient;
    @Mock private BookCacheService bookCacheService;
    @Mock private GroqAiService aiService;

    @InjectMocks
    private BookRecommendationService service;

    // -----------------------------------------------------------------
    // CheckUserStrategy
    // -----------------------------------------------------------------
    @Test
    void testCheckUserStrategy_LowHistoryRatio() {
        UserDto user = new UserDto();
        user.setUserId(1);
        user.setCity("תל אביב");

        // הדמיית היסטוריית השאלות הנדרשת בתוך המתודה
        when(loanClient.getLoanHistory(1)).thenReturn(List.of(new LoanHistoryDto()));

        String strategy = service.CheckUserStrategy(user);
        assertTrue(strategy.contains("10% מרחק") || strategy.contains("50% גיל"));
    }

    @Test
    void testCheckUserStrategy_Logic() {
        UserDto user = new UserDto();
        user.setUserId(1);
        user.setCity("תל אביב");

        List<LoanHistoryDto> history = new ArrayList<>();
        LoanHistoryDto loan1 = new LoanHistoryDto();
        loan1.setBookId(101);
        loan1.setBranchCity("תל אביב");
        history.add(loan1);

        LoanHistoryDto loan2 = new LoanHistoryDto();
        loan2.setBookId(102);
        loan2.setBranchCity("חיפה");
        history.add(loan2);

        when(loanClient.getLoanHistory(1)).thenReturn(history);

        String strategy = service.CheckUserStrategy(user);
        assertTrue(strategy.contains("10% מרחק"));
    }

    // -----------------------------------------------------------------
    // BuildAiPrompt
    // -----------------------------------------------------------------
    @Test
    void testBuildAiPrompt_ContainsRequiredFormat() {
        when(bookCacheService.getBooks()).thenReturn(List.of(new BookDto(1, "Title", "Category", "City")));

        String prompt = service.BuildAiPrompt("Test Strategy");
        assertTrue(prompt.contains("bookIds"));
        assertTrue(prompt.contains("reasonForChoice"));
    }

    // -----------------------------------------------------------------
    // ExecuteAiRequest
    // -----------------------------------------------------------------
    @Test
    void testExecuteAiRequest_ValidJson() {
        String jsonPrompt = "{\"bookIds\": [1, 2], \"reasonForChoice\": \"Good match\"}";

        BookRecommendationDto result = service.executeAiRequest(jsonPrompt);

        assertNotNull(result);
        assertEquals("Good match", result.getReasonForChoice());
        assertNotNull(result.getGeneratedDate());
    }

    @Test
    void testExecuteAiRequest_InvalidJson() {
        String invalidJson = "{ invalid }";

        BookRecommendationDto result = service.executeAiRequest(invalidJson);

        assertNotNull(result);
        assertNull(result.getReasonForChoice());
    }

    // -----------------------------------------------------------------
    // SaveRecommendations
    // -----------------------------------------------------------------
    @Test
    void testSaveRecommendations_DeletesAndSaves() {
        int userId = 1;
        BookRecommendationDto dto = new BookRecommendationDto();
        dto.setBookIds(List.of(101, 102));
        dto.setReasonForChoice("Reason");
        dto.setGeneratedDate(LocalDateTime.now());

        boolean result = service.saveRecommendations(userId, dto);

        assertTrue(result);
        verify(repository, times(1)).deleteByUserId(userId);
        verify(repository, times(1)).save(any(BookRecommendation.class));
    }

    @Test
    void testSaveRecommendations_InvalidDto_ReturnsFalse() {
        boolean result = service.saveRecommendations(1, null);

        assertFalse(result);
        verify(repository, never()).deleteByUserId(anyInt());
        verify(repository, never()).save(any());
    }

    // -----------------------------------------------------------------
    // GetRecommendationsForUser (Void Methods Fixed)
    // -----------------------------------------------------------------
    @Test
    void testGetRecommendationsForUser_Success() {
        UserDto mockUser = new UserDto();
        mockUser.setUserId(1);
        mockUser.setCity("תל אביב");
        String mockAiResponse = "{\"bookIds\": [1, 2], \"reasonForChoice\": \"ניסוי\"}";

        when(loanClient.getLoanHistory(1)).thenReturn(new ArrayList<>());
        when(bookCacheService.getBooks()).thenReturn(new ArrayList<>());
        when(aiService.generateAsync(anyString())).thenReturn(mockAiResponse);

        // תוקן: קריאה כ-void ללא השמה למשתנה
        service.getRecommendationsForUser(mockUser);

        verify(repository, times(1)).save(any());
    }

    @Test
    void testGetRecommendationsForUser_FullFlow_Success() {
        UserDto mockUser = new UserDto();
        mockUser.setUserId(1);
        mockUser.setCity("תל אביב");

        when(loanClient.getLoanHistory(1)).thenReturn(new ArrayList<>());
        when(bookCacheService.getBooks()).thenReturn(new ArrayList<>());

        String mockAiResponse = "{\"bookIds\": [1, 2], \"reasonForChoice\": \"בגלל מיקום ותחומי עניין\"}";
        when(aiService.generateAsync(anyString())).thenReturn(mockAiResponse);

        // תוקן: קריאה כ-void ללא השמה למשתנה
        service.getRecommendationsForUser(mockUser);

        verify(aiService, times(1)).generateAsync(anyString());
        verify(repository, times(1)).save(any(BookRecommendation.class));
    }

    @Test
    void testGetRecommendationsForUser_FullFlow() {
        UserDto user = new UserDto();
        user.setUserId(1);
        user.setCity("תל אביב");

        String aiJsonResponse = "{\"bookIds\": [1, 2], \"reasonForChoice\": \"בחירה מבוססת מיקום\"}";

        when(loanClient.getLoanHistory(1)).thenReturn(new ArrayList<>());
        when(bookCacheService.getBooks()).thenReturn(new ArrayList<>());
        when(aiService.generateAsync(anyString())).thenReturn(aiJsonResponse);

        // תוקן: קריאה כ-void ללא השמה למשתנה
        service.getRecommendationsForUser(user);

        verify(repository, times(1)).save(any(BookRecommendation.class));
        verify(aiService, times(1)).generateAsync(anyString());
    }

    // -----------------------------------------------------------------
    // GetUserById
    // -----------------------------------------------------------------
    @Test
    void testGetUserById_CallsClientCorrectly() {
        int userId = 5;
        UserDto mockUser = new UserDto();
        mockUser.setUserId(userId);

        when(userClient.getUserById(userId)).thenReturn(mockUser);

        UserDto result = service.GetUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(userClient, times(1)).getUserById(userId);
    }

    // -----------------------------------------------------------------
    // Delete
    // -----------------------------------------------------------------
    @Test
    void testDelete_CallsRepository() {
        int userId = 1;
        service.delete(userId);
        verify(repository, times(1)).deleteByUserId(userId);
    }

    // -----------------------------------------------------------------
    // Add
    // -----------------------------------------------------------------
    @Test
    void testAdd_InitializationSuccess() {
        int userId = 99;
        UserDto mockUser = new UserDto();
        mockUser.setUserId(userId);
        mockUser.setCity("תל אביב");

        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userClient.getUserById(userId)).thenReturn(mockUser);
        when(loanClient.getLoanHistory(userId)).thenReturn(new ArrayList<>());
        when(bookCacheService.getBooks()).thenReturn(new ArrayList<>());
        when(aiService.generateAsync(anyString())).thenReturn("{\"bookIds\": [1], \"reasonForChoice\": \"test\"}");

        boolean result = service.add(userId);

        assertTrue(result);
        verify(repository, times(2)).save(any(BookRecommendation.class)); // פעם באתחול ופעם בתוך getRecommendationsForUser
    }
}