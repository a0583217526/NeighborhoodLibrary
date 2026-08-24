package com.library.smart_library_ai.controller;

import com.library.smart_library_ai.dto.BookRecommendationDto;
import com.library.smart_library_ai.service.BookRecommendationService;
import com.library.smart_library_ai.service.clients.BookClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final BookRecommendationService service;

    public RecommendationController(BookRecommendationService service) {
        this.service = service;
    }
    @GetMapping("/start")
    public String start(@PathVariable int userId) {

       return "working";
    }
    // 1. getWeeklyUpdates – מחזיר את ההמלצה השבועית לפי userId
    @GetMapping("/{userId}")
    public ResponseEntity<BookRecommendationDto> getUserRecommendations(@PathVariable int userId) {
        try {
            BookRecommendationDto result = service.getRecommendationFromDB(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    // 4. refresh – רענון המלצות ידני (מפעיל את ה-AI מחדש)
//    @PostMapping("/refresh/{userId}")
//    public ResponseEntity<BookRecommendationDto> refreshRecommendations(@PathVariable int userId) {
//        try {
//            // שליפת המשתמש המלא כולל הנתונים לצורך העיבוד
//            var user = service.GetUserById(userId);
//
//            // הפעלת האורקסטרטור המלא של ה-AI
//            BookRecommendationDto refreshedResult = service.getRecommendationsForUser(user.getUserId());
//
//            return ResponseEntity.ok(refreshedResult);
//        } catch (Exception e) {
//            System.err.println("Error during manual refresh for user " + userId + ": " + e.getMessage());
//            // מחזירים 500 במקרה של כשל ב-AI או ב-DB
//            return ResponseEntity.internalServerError().build();
//        }
//    }
    // 2. add – יצירת Recommendation בסיסי למשתמש חדש
    @PostMapping("/add/{userId}")
    public boolean add(
            @PathVariable int userId) {

        boolean created = service.add(userId);
        return created;
    }

    // 3. delete – מחיקת Recommendation של משתמש (כולל כל הדאטה)
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable int userId) {
        service.delete(userId);
        return ResponseEntity.ok().build();
    }

}



