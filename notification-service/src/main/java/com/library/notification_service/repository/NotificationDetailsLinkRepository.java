package com.library.notification_service.repository;

import com.library.notification_service.entity.NotificationDetailsLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationDetailsLinkRepository extends JpaRepository<NotificationDetailsLink, Integer> {
    List<NotificationDetailsLink> findByNotificationId(Integer notificationId);

}
