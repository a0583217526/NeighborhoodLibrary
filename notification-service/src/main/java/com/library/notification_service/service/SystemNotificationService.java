package com.library.notification_service.service;

import com.library.notification_service.DTO.AdditionalDetailDTO;
import com.library.notification_service.DTO.SystemNotificationDTO;
import com.library.notification_service.DTO.UserNotificationDTO;
import com.library.notification_service.entity.Content;
import com.library.notification_service.entity.Notification;
import com.library.notification_service.entity.UserNotification;
import com.library.notification_service.repository.ContentRepository;
import com.library.notification_service.repository.NotificationRepository;
import com.library.notification_service.repository.UserNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor// מסמן ל-Spring ליצור מופע (Bean) ישיר של המחלקה הזו
public class SystemNotificationService {


    private final UserNotificationRepository userNotificationRepository;
    private final NotificationDetailsLinkService notificationDetailsLinkService;
    private final ContentRepository contentRepository;
    private final NotificationRepository notificationRepository;
    private final TypeAdditionalService typeAdditionalService;
    private final NotificationService notificationService;
    private final EmailService emailService;




    /**
     * תרחיש 1: מייל ברוכים הבאים
     * טריגר: רישום מוצלח ראשוני של משתמש חדש
     *       אנו משתמשים ב-@Transactional כדי להבטיח שכל פעולות ה-DB יתבצעו כיחידה אחת.
     */
    @Async
    @Transactional
    public void sendWelcomeEmail(UserNotificationDTO userDTO) {
        UserNotification toUser=userNotificationRepository.findByEmail(userDTO.getEmail());
        // בדיקה האם המשתמש כבר קיים במסד הנתונים *שלנו* (לפי ה-ID המדויק מהסרוויס החיצוני)
        if (toUser == null) {
            toUser = new UserNotification();
            toUser.setEmail(userDTO.getEmail());}
            // שימו לב: אנו מעתיקים את ה-ID המדויק, אותה ישות ואותו ערך, ללא ייצור אוטומטי!
            toUser.setUserId(userDTO.getId());
            toUser.setName(userDTO.getName());

            toUser.setPhone(userDTO.getPhone());

            toUser = userNotificationRepository.saveAndFlush(toUser);
            System.out.println("משתמש חדש סונכרן ונשמר בהצלחה בטבלה המקומית: " + toUser.getName());

        Content content = contentRepository.findContentById(1);
        if (content == null) {
            throw new IllegalArgumentException("שגיאה קריטית: תבנית מייל ברוכים הבאים (ID=1) לא קיימת במסד הנתונים!");
        }

        Notification notification = new Notification(null, null, toUser, "PENDING", content, LocalDateTime.now(),0, false,null);

        notification = notificationRepository.saveAndFlush(notification);

        Map<String, String> templateData = new HashMap<>();

        SystemNotificationDTO systemNotificationDTO = new SystemNotificationDTO();
        systemNotificationDTO.setToUserId(toUser.getUserId());

        templateData = typeAdditionalService.addUserToMap(templateData, systemNotificationDTO);

        String finalHtmlBody = notificationService.processTemplate(content.getContentNotification(), templateData);

        System.out.println("--- ניסיון שליחת מייל ברוכים הבאים ל- '" + toUser.getEmail() + "' ---");

        boolean isSendSuccessful = emailService.sendEmail(toUser.getEmail(), content.getSubject(), finalHtmlBody);

        if (isSendSuccessful) {
            notification.setStatus("SENT");
            System.out.println("סטטוס ההתראה עודכן בהצלחה ל-SENT");
        } else {
            notification.setStatus("FAILED");
            System.err.println("שליחת המייל נכשלה. סטטוס ההתראה עודכן ל-FAILED לצורך מנגנון ה-Retry");
        }

        notificationRepository.save(notification);
    }

    /**
     * תרחיש 5: ספר מבוקש התפנה
     * טריגר: ספר שהיה מושאל חזר למלאי
     */
    @Async
    @Transactional
    public void sendBookAvailableEmail(SystemNotificationDTO dto) {

        UserNotification toUser = userNotificationRepository.findByUserId(dto.getToUserId());

        if (toUser == null) {
            throw new IllegalArgumentException("שגיאה: לא נמצא משתמש במערכת ההתראות עבור מזהה: " + dto.getToUserId());
        }

        Content content = contentRepository.findContentById(5);
        if (content == null) {
            throw new IllegalArgumentException("שגיאה קריטית: תבנית ספר זמין (ID=5) לא קיימת במסד הנתונים!");
        }

        Notification notification = new Notification(null, null, toUser, "PENDING", content, LocalDateTime.now(),0, false, null);

        notification = notificationRepository.saveAndFlush(notification);

        notificationDetailsLinkService.saveNotificationLinks(notification, dto.getAdditionalDetails());

        // 5. המרת רשימת הפרטים הדינמיים מה-DTO למפת ערכים (Map) לצורך ההחלפה בתבנית
        // למשל המפה תכיל: "bookName" -> "לב של קרח", "bookId" -> "12", "actionLink" -> "http..."
        Map<String, String> templateMap = typeAdditionalService.convertDetailsListToMap(dto.getAdditionalDetails());

        // 6. הזרקת שם המשתמש הנמען לתוך ה-Map באמצעות הפונקציה שלך
        // היא תכניס למפה את הזוג: "toUser" -> "שם המשתמש האמיתי"
        templateMap = typeAdditionalService.addUserToMap(templateMap, dto);

        // 7. הרכבת ה-HTML הסופי (Parsing)
        // מחליף את {toUser}, {bookName} וכל פלייס-הולדר אחר בערכים האמיתיים מתוך ה-Map
        String finalHtmlBody = notificationService.processTemplate(content.getContentNotification(), templateMap);

        boolean isSendSuccessful = emailService.sendEmail(toUser.getEmail(), content.getSubject(), finalHtmlBody);

        if (isSendSuccessful) {
            notification.setStatus("SENT");
            System.out.println("המייל נשלח בהצלחה! הסטטוס עודכן ל-SENT במסד הנתונים.");
        } else {
            notification.setStatus("FAILED");
            System.err.println("שליחת המייל נכשלה! הסטטוס סומן כ-FAILED עבור מנגנון ה-Retry.");
        }

        notificationRepository.save(notification);
    }

    /**
     * תרחיש 6: אימות והחלפת סיסמה
     * טריגר: בקשה לשינוי סיסמה או שינוי בפועל
     */
    public void sendPasswordResetEmail(SystemNotificationDTO dto) {
        Notification notification = new Notification();
        UserNotification toUser = null;
        String targetEmail;
        if (dto.getToUserId() != null) {
            toUser = userNotificationRepository.findByUserId(dto.getToUserId());
        }
        else {
            toUser=new UserNotification();
            toUser.setEmail( dto.getRecipientEmail());
            toUser = userNotificationRepository.saveAndFlush(toUser);
        }
        notification.setToUser(toUser);
        notification.setStatus("PENDING");
        Content content = contentRepository.findById(6)
                .orElseThrow(() -> new RuntimeException("Content not found!"));

        notification.setContent(content);
        notification.setSendDate(LocalDateTime.now());
        Notification saveNotification = notificationRepository.saveAndFlush(notification);
        notificationDetailsLinkService.saveNotificationLinks(saveNotification, dto.getAdditionalDetails());
        Map<String, String> mapDetails = typeAdditionalService.convertDetailsListToMap(dto.getAdditionalDetails());
        String templateContent = notificationService.processTemplate(content.getContentNotification(), mapDetails);
        boolean isSendSuccessful = emailService.sendEmail(toUser.getEmail(), content.getSubject(), templateContent);
        if (isSendSuccessful) {
            notification.setStatus("SENT");
            System.out.println("סטטוס ההתראה עודכן בהצלחה ל-SENT");
        } else {
            notification.setStatus("FAILED");
            System.err.println("שליחת המייל נכשלה. סטטוס ההתראה עודכן ל-FAILED לצורך מנגנון ה-Retry");
        }

    }

    /**
     * תרחיש 7: תזכורת ידידותית לפני החזרה (מתוזמן)
     * טריגר: 3 ימים לפני מועד ההחזרה (מופעל ע"י קרונז'וב או סרוויס השאלות)
     */
    public void sendReturnReminder(Long borrowerId, Long bookId, LocalDate dueDate) {
        // TODO: לוגיקת שליחת תזכורת
        System.out.println("Processing return reminder for borrower: " + borrowerId + " due on: " + dueDate);
    }

    /**
     * תרחיש 8: סיכום סטטיסטי חודשי (מתוזמן)
     * טריגר: 1 לכל חודש קלנדרי
     */
    public void sendMonthlyDigest(Long userId, Object statsDto) {
        // TODO: לוגיקת הרכבת דוח חודשי (Gamification)
        System.out.println("Processing monthly digest for user: " + userId);
    }

    @Scheduled(fixedRate = 900000)
    @Transactional
    public void retryFailedNotifications() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        // שליפת ההודעות שנכשלו, שבוצעו פחות מ-3 פעמים בטווח הזמן הרלוונטי
        List<Notification> failedNotifications = notificationRepository
                .findByStatusAndRetryCountLessThanAndSendDateAfter("FAILED", 3, twentyFourHoursAgo);

        for (Notification notification : failedNotifications) {
            try {
                // 1. בדיקת תקינות המשתמש לפני השליחה (מונע NullPointerException)
                // בדיקת תקינות נמען
                if (notification.getToUser() == null || notification.getToUser().getEmail() == null) {
                    continue;
                }

                // 2. שליפת פרטים ועיבוד התבנית
                List<AdditionalDetailDTO> listDetails = typeAdditionalService.getContentTypeList(notification);
                Map<String, String> mapDetails = typeAdditionalService.convertDetailsListToMap(listDetails);

                String templateContent = notificationService.processTemplate(notification.getContent().getContentNotification(), mapDetails);
                String templateSubject = notificationService.processTemplate(notification.getContent().getSubject(), mapDetails);

                // 3. שליחה בפועל
                boolean isSendSuccessful = emailService.sendEmail(notification.getToUser().getEmail(), templateSubject, templateContent);

                if (isSendSuccessful) {
                    notification.setStatus("SENT");
                    notification.setRetryCount(0); // איפוס מונה בהצלחה
                } else {
                    // הגדלת מונה בכישלון
                    notification.setRetryCount(notification.getRetryCount() + 1);
                }

                // עדכון זמן הניסיון האחרון ושמירה
                notification.setSendDate(LocalDateTime.now());
                notificationRepository.save(notification);

            } catch (Exception e) {
                // אם משהו בלוגיקה נכשל (למשל בעיה בעיבוד התבנית), אנחנו לא רוצים שהלולאה תעצור
                System.err.println("Error processing notification ID " + notification.getId() + ": " + e.getMessage());
                // אופציונלי: אפשר לעדכן פה סטטוס ל-"ERROR" כדי להבדיל בין כישלון שליחה לכישלון לוגי
            }
        }
    }
}