package com.library.smart_library_ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestConnectionController {

    // כלי מובנה של Spring שמאפשר להריץ שאילתות SQL ישירות ובקלות
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> checkConnection() {
        Map<String, Object> response = new HashMap<>();

        try {
            // אנחנו מריצים שאילתה פשוטה מאוד שרק בודקת שה-DB של ה-PostgreSQL ב-Docker עונה לנו
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            if (result != null && result == 1) {
                response.put("status", "Success");
                response.put("message", "Spring Boot Context is UP, and PostgreSQL in Docker is CONNECTED successfully!");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Error");
                response.put("message", "Connected to something, but database behavior is unexpected.");
                return ResponseEntity.status(500).body(response);
            }

        } catch (Exception e) {
            // אם ה-Docker כבוי או שההגדרות ב-application.properties שגויות, הקוד יגיע לכאן:
            response.put("status", "Failure");
            response.put("message", "Spring Boot is running, BUT connection to Docker PostgreSQL failed.");
            response.put("errorDetails", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}