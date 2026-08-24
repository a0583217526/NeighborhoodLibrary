package com.library.notification_service.controller;

import com.library.notification_service.DTO.SystemNotificationDTO;
import com.library.notification_service.DTO.UserNotificationDTO;
import com.library.notification_service.service.SystemNotificationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/notifications/system")
public class SystemNotificationController {

    private final SystemNotificationService systemNotificationService;

    // הזרקה ישירה של ה-Service דרך ה-Constructor
    public SystemNotificationController(SystemNotificationService systemNotificationService) {
        this.systemNotificationService = systemNotificationService;
    }

    @PostMapping("/welcome")
    public ResponseEntity<Void> triggerWelcomeEmail(@RequestBody UserNotificationDTO userDTO) {
        systemNotificationService.sendWelcomeEmail(userDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/wishlist-alert")
    public ResponseEntity<Void> triggerWishlistAlert(@RequestBody SystemNotificationDTO dto) {
        systemNotificationService.sendBookAvailableEmail(dto);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> triggerPasswordReset(@RequestBody SystemNotificationDTO dto) {
        systemNotificationService.sendPasswordResetEmail(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/return-reminder")
    public ResponseEntity<Void> triggerReturnReminder(
            @RequestParam Long borrowerId, 
            @RequestParam Long bookId, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        systemNotificationService.sendReturnReminder(borrowerId, bookId, dueDate);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/monthly-digest/{userId}")
    public ResponseEntity<Void> triggerMonthlyDigest(@PathVariable Long userId, @RequestBody Object statsDto) {
        systemNotificationService.sendMonthlyDigest(userId, statsDto);
        return ResponseEntity.ok().build();
    }
}