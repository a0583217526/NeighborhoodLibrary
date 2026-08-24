package com.library.notification_service.service;

import com.library.notification_service.DTO.AdditionalDetailDTO;
import com.library.notification_service.DTO.SystemNotificationDTO;
import com.library.notification_service.DTO.UserNotificationDTO;
import com.library.notification_service.DTO.UserToUserNotificationDTO;
import com.library.notification_service.entity.Notification;
import com.library.notification_service.entity.NotificationDetailsLink;
import com.library.notification_service.entity.TypeAdditionalDetails;
import com.library.notification_service.entity.UserNotification;
import com.library.notification_service.repository.NotificationDetailsLinkRepository;
import com.library.notification_service.repository.TypeAdditionalRepository;
import com.library.notification_service.repository.UserNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TypeAdditionalService {
    private final TypeAdditionalRepository typeRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationDetailsLinkRepository NotificationLinkRepository;


    public Map<String, String> convertDetailsListToMap(List<AdditionalDetailDTO> detailsList) {

        Map<String, String> templateData = new HashMap<>();

        // בדיקת תקינות - אם הרשימה ריקה, נחזיר מילון ריק
        if (detailsList == null || detailsList.isEmpty()) {
            return templateData;
        }

        // 1. שולפים את כל מספרי ה-ID מתוך הרשימה שהגיעה
        List<Integer> typeIds = detailsList.stream()
                .map(AdditionalDetailDTO::getTypeId)
                .collect(Collectors.toList());

        // 2. פונים למסד הנתונים *פעם אחת בלבד* ושולפים את כל סוגי הנתונים הרלוונטיים
        List<TypeAdditionalDetails> typesFromDb = typeRepository.findAllById(typeIds);

        // 3. הופכים את התוצאה מהמסד למילון עזר פנימי (ID -> TypeDetail) לחיפוש מהיר
        Map<Integer, String> idToNameMap = typesFromDb.stream()
                .collect(Collectors.toMap(TypeAdditionalDetails::getId, TypeAdditionalDetails::getTypeDetail));

        // 4. עוברים על הרשימה המקורית ובונים את ה-Map הסופי שמיועד לתבנית
        for (AdditionalDetailDTO detail : detailsList) {
            // שולפים את השם הטקסטואלי (למשל "fromUser") לפי ה-ID
            String keyName = idToNameMap.get(detail.getTypeId());

            if (keyName != null) {
                // מכניסים ל-Map הסופי: מפתח = שם המשתנה, ערך = התוכן שנשלח
                templateData.put(keyName, detail.getValue());
            } else {
                // במקרה ששלחו ID שלא קיים במסד הנתונים
                throw new IllegalArgumentException("שגיאה: לא נמצא סוג נתון במסד עבור מזהה: " + detail.getTypeId());
            }
        }

        return templateData;
    }
    public Map<String , String> addUsersToMap (Map<String, String> maps, UserToUserNotificationDTO dto){
        UserNotification fromUser = userNotificationRepository.findByUserId(dto.getFromUserId());
        UserNotification toUser = userNotificationRepository.findByUserId(dto.getToUserId());
        maps.put("fromUser", fromUser.getName());
        maps.put("toUser", toUser.getName());
        return  maps;
    }
    public Map<String , String> addUserToMap (Map<String, String> maps, SystemNotificationDTO dto){
        UserNotification toUser = userNotificationRepository.findByUserId(dto.getToUserId());
        maps.put("toUser", toUser.getName());
        return  maps;
    }
    public List<AdditionalDetailDTO> getContentTypeList(Notification notification) {
        // קריאה ל-Repository כדי לקבל את הרשימה
        List<NotificationDetailsLink> links = NotificationLinkRepository.findByNotificationId(notification.getId());

        // המרת הרשימה לרשימת מחרוזות (ContentTypes)
        return links.stream()
                .map(link -> {
                    AdditionalDetailDTO dto = new AdditionalDetailDTO();
                    // מניח שיש לך גישה לאובייקט ה-type בתוך ה-link
                    dto.setTypeId(link.getType().getId());
                    dto.setValue(link.getContentType());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
