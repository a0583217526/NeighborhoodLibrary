package com.library.notification_service.controller;



import com.library.notification_service.entity.Notification;
import com.library.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public Page<Notification> getUserNotifications(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page) {

        return notificationService.getNotifications(userId, page);
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable UUID userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }


    // איפוס וסימון כל ההודעות כנקראו לפי UUID
    @PutMapping("/user/{userId}/read")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable UUID userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

}