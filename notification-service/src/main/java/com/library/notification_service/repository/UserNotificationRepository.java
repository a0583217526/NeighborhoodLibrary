package com.library.notification_service.repository;

import com.library.notification_service.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {
    UserNotification findUserById(Integer fromUserId);
    // שליפה לצורך עדכון משתמש בתהליך רישום
    UserNotification findByEmail(String email);

    // שליפה לצורך פעולות מול השירות החיצוני
    UserNotification findByUserId(UUID UserId);
}
