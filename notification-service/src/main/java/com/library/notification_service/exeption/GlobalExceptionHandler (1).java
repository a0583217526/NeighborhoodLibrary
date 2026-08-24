package com.library.notification_service.exeption;





import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", "RUNTIME_ERROR");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("timestamp", System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", "INTERNAL_SERVER_ERROR");
        body.put("message", "שגיאה פנימית בשרת");
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("timestamp", System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}