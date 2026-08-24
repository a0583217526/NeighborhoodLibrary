package com.library.notification_service.service;
import com.library.notification_service.entity.Notification;
import com.library.notification_service.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    //פונקציה ליצירת תוכן מושלם תקין,  לפי MAP מסויים שהכניסו+תבנית
    public String processTemplate(String templateText, Map<String, String> templateData) {
        String processedText = templateText;

        // מעבר על כל הנתונים במילון והחלפתם בטקסט
        for (Map.Entry<String, String> entry : templateData.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            processedText = processedText.replace(placeholder, entry.getValue());
        }

        return processedText;
    }
    public Page<Notification> getNotifications(UUID userId, int page) {

        Pageable pageable = PageRequest.of(page, 20);

        return notificationRepository.findByToUserUserIdOrderBySendDateDesc(
                userId,
                pageable
        );
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByToUserAndIsReadFalse(userId);
    }

    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }
}
