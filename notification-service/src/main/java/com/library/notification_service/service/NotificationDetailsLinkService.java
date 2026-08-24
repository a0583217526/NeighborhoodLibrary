package com.library.notification_service.service;

import com.library.notification_service.DTO.AdditionalDetailDTO;
import com.library.notification_service.DTO.UserToUserNotificationDTO;
import com.library.notification_service.entity.Notification;
import com.library.notification_service.entity.NotificationDetailsLink;
import com.library.notification_service.entity.TypeAdditionalDetails;
import com.library.notification_service.repository.NotificationDetailsLinkRepository;
import com.library.notification_service.repository.TypeAdditionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationDetailsLinkService {
    private final NotificationDetailsLinkRepository linkRepository;
    private final TypeAdditionalRepository typeRepository;

    /**
     * פונקציה המקבלת את ההתראה שנשמרה ואת ה-DTO עם רשימת הפרטים הנוספים,
     * ממירה אותם לישויות ושומרת בטבלת הקישור.
     */
    @Transactional
    public void saveNotificationLinks(Notification savedNotification, List<AdditionalDetailDTO> additionalDetails) {

        // וידוא שהרשימה לא ריקה כדי למנוע שגיאות (NullPointerException)
        if (additionalDetails == null || additionalDetails.isEmpty()) {
            return;
        }

        // רשימה לאיסוף כל הקישורים שניצור לפני השמירה
        List<NotificationDetailsLink> linksToSave = new ArrayList<>();

        // מעבר על כל פריט ברשימת ה-DTO
        for (AdditionalDetailDTO detailDTO : additionalDetails) {

            NotificationDetailsLink link = new NotificationDetailsLink();

            // 1. הכנסת ההתראה (ה-Notification שכבר שמרנו ויש לו ID)
            link.setNotification(savedNotification);

            // 2. הכנסת סוג הנתון (TypeAdditionalDetails) בעזרת רפרנס בלבד
            TypeAdditionalDetails typeRef = typeRepository.getTypeById(detailDTO.getTypeId());
            link.setType(typeRef);

            // 3. הכנסת התוכן עצמו (ה-value)
            link.setContentType(detailDTO.getValue());

            // הוספת האובייקט המוכן לרשימה
            linksToSave.add(link);
        }

        linkRepository.saveAll(linksToSave);
    }
}