package com.library.notification_service.service;

import com.library.notification_service.DTO.UserToUserNotificationDTO;
import com.library.notification_service.entity.Content;
import com.library.notification_service.entity.Notification;
import com.library.notification_service.entity.UserNotification;
import com.library.notification_service.repository.ContentRepository;
import com.library.notification_service.repository.NotificationRepository;
import com.library.notification_service.repository.UserNotificationRepository;
import org.springframework.boot.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Service
public class UserMediationNotificationService {


    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final ContentRepository contentRepository;
    private final NotificationDetailsLinkService notificationDetailsLinkService;
    private final TypeAdditionalService typeAdditionalService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public UserMediationNotificationService(NotificationRepository notificationRepository, UserNotificationRepository userNotificationRepository, ContentRepository contentRepository
                                            , NotificationDetailsLinkService notificationDetailsLinkService
            ,TypeAdditionalService typeAdditionalService , NotificationService notificationService, EmailService emailService) {

        this.notificationRepository= notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.contentRepository = contentRepository;
        this.notificationDetailsLinkService = notificationDetailsLinkService;
        this.typeAdditionalService = typeAdditionalService;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    // private final NotificationRepository notificationRepository;
    // private final JavaMailSender mailSender;

    /**
     * תרחיש 2: בקשת השאלה חדשה
     * טריגר: משתמש א' מבקש להשאיל ספר של משתמש ב'
     */
    public void sendLoanRequestAlert(
                                     @RequestBody UserToUserNotificationDTO dto  ) {
      Notification notification=new Notification();
        UserNotification toUser = userNotificationRepository.findByUserId(dto.getToUserId());
        if (toUser == null) {
            throw new IllegalArgumentException("שגיאה: לא נמצא משתמש שואל (נמען) עבור מזהה: " + dto.getToUserId());
        }

        UserNotification fromUser = userNotificationRepository.findByUserId(dto.getFromUserId());
        if (fromUser == null) {
            throw new IllegalArgumentException("שגיאה: לא נמצא משתמש בעל הספר (שולח) עבור מזהה: " + dto.getFromUserId());
        }
        notification.setFromUser(fromUser);
        notification.setStatus("PENDING");
        Content content = contentRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Content not found!"));

        notification.setContent(content);
        notification.setSendDate(LocalDateTime.now());
        Notification saveNotification= notificationRepository.saveAndFlush(notification);
        notificationDetailsLinkService.saveNotificationLinks(saveNotification,dto.getAdditionalDetails());
        Map<String,String> mapDetails=typeAdditionalService.convertDetailsListToMap(dto.getAdditionalDetails());
        mapDetails=typeAdditionalService.addUsersToMap(mapDetails,dto);
        String templateContent=notificationService.processTemplate(content.getContentNotification(),mapDetails);
        boolean isSendSuccessful = emailService.sendEmail(toUser.getEmail(),content.getSubject(),templateContent);
        notification.setStatus(isSendSuccessful ? "SENT" : "FAILED");
    }

    /**
     * תרחיש 3: אישור בקשת השאלה
     * טריגר: בעל הספר אישר את בקשת ההשאלה
     *  פונקציה אסינכרונית לעיבוד ושליחת מייל "אישור בקשת השאלה".
     */

    @Async
    @Transactional
    public void sendLoanApprovalNotification(UserToUserNotificationDTO dto) {

        UserNotification toUser = userNotificationRepository.findByUserId(dto.getToUserId());
        if (toUser == null) {
            throw new IllegalArgumentException("שגיאה: לא נמצא משתמש שואל (נמען) עבור מזהה: " + dto.getToUserId());
        }

        UserNotification fromUser = userNotificationRepository.findByUserId(dto.getFromUserId());
        if (fromUser == null) {
            throw new IllegalArgumentException("שגיאה: לא נמצא משתמש בעל הספר (שולח) עבור מזהה: " + dto.getFromUserId());
        }

        Content content = contentRepository.findContentById(3);
        if (content == null) {
            throw new IllegalArgumentException("שגיאה קריטית: תבנית אישור השאלה (ID=3) לא קיימת במסד הנתונים!");
        }

        Notification notification = new Notification(null, fromUser, toUser, "PENDING", content, LocalDateTime.now(), 0, false, null);
        notification = notificationRepository.saveAndFlush(notification);

        notificationDetailsLinkService.saveNotificationLinks(notification, dto.getAdditionalDetails());

        Map<String, String> templateMap = typeAdditionalService.convertDetailsListToMap(dto.getAdditionalDetails());

        templateMap = typeAdditionalService.addUsersToMap(templateMap, dto);

        String finalHtmlBody = notificationService.processTemplate(content.getContentNotification(), templateMap);

        System.out.println("--- ניסיון שליחת מייל אישור השאלה ל- '" + toUser.getEmail() + "' ---");
        boolean isSendSuccessful = emailService.sendEmail(toUser.getEmail(), content.getSubject(), finalHtmlBody);

        if (isSendSuccessful) {
            notification.setStatus("SENT");
            System.out.println("מייל אישור השאלה נשלח בהצלחה! הסטטוס עודכן ל-SENT.");
        } else {
            notification.setStatus("FAILED");
            System.err.println("שליחת המייל נכשלה! הסטטוס עודכן ל-FAILED לצורך ריצת ה-Retry.");
        }

        notificationRepository.save(notification);
    }


    /**
     * תרחיש 4: בקשת הארכת זמן השאלה
     * טריגר: השואל מבקש להאריך את תקופת ההחזרה
     */


    public void sendLoanExtensionRequest(UserToUserNotificationDTO userToUserNotificationDTO) {
        UserNotification fromUser=userNotificationRepository.findByUserId(userToUserNotificationDTO.getFromUserId());
        UserNotification toUser=userNotificationRepository.findByUserId(userToUserNotificationDTO.getToUserId());
        System.out.println(toUser.getName());
        Content content=contentRepository.findContentById(4);

        Notification notification=new Notification(null,fromUser,toUser,"PENDING",content, LocalDateTime.now(),0, false, null);
        notification=notificationRepository.saveAndFlush(notification);
        notificationDetailsLinkService.saveNotificationLinks(notification,userToUserNotificationDTO.getAdditionalDetails());
        Map<String,String> convertMap=typeAdditionalService.convertDetailsListToMap(userToUserNotificationDTO.getAdditionalDetails());
        String template=notificationService.processTemplate(content.getContentNotification(),convertMap);

        System.out.println("--- בדיקת כתובות מייל לפני שליחה ---");
        System.out.println("To Email: '" + toUser.getEmail() + "'");
        System.out.println("Subject: " + content.getSubject());

        //שימו לב להוסיף בדיקה האם נשלח או לא ולעדכן סטטוס בהתאם
        boolean isSendSuccessful = emailService.sendEmail(toUser.getEmail(), content.getSubject(), template);

        notification.setStatus(isSendSuccessful ? "SENT" : "FAILED");
    }
    /**
     * תרחיש 9: בקשת הארכת זמן השאלה
     * טריגר: השואל מבקש להאריך את תקופת ההחזרה
     */
    public void approveLoanExtensionRequest(UserToUserNotificationDTO dto) {

        UserNotification toUser = userNotificationRepository.findByUserId(dto.getToUserId());
        if (toUser == null) {
            throw new IllegalArgumentException("שגיאה: לא נמצא משתמש שואל (נמען) עבור מזהה: " + dto.getToUserId());
        }

        UserNotification fromUser = userNotificationRepository.findByUserId(dto.getFromUserId());
        if (fromUser == null) {
            throw new IllegalArgumentException("שגיאה: לא נמצא משתמש בעל הספר (שולח) עבור מזהה: " + dto.getFromUserId());
        }

        Content content = contentRepository.findContentById(9);
        if (content == null) {
            throw new IllegalArgumentException("שגיאה קריטית: תבנית אישור השאלה (ID=3) לא קיימת במסד הנתונים!");
        }

        Notification notification = new Notification(null, fromUser, toUser, "PENDING", content, LocalDateTime.now(), 0, false, null);
        notification = notificationRepository.saveAndFlush(notification);

        notificationDetailsLinkService.saveNotificationLinks(notification, dto.getAdditionalDetails());

        Map<String, String> templateMap = typeAdditionalService.convertDetailsListToMap(dto.getAdditionalDetails());

        templateMap = typeAdditionalService.addUsersToMap(templateMap, dto);

        String finalHtmlBody = notificationService.processTemplate(content.getContentNotification(), templateMap);

        System.out.println("--- ניסיון שליחת מייל אישור השאלה ל- '" + toUser.getEmail() + "' ---");
        boolean isSendSuccessful = emailService.sendEmail(toUser.getEmail(), content.getSubject(), finalHtmlBody);

        notification.setStatus(isSendSuccessful ? "SENT" : "FAILED");

    }

}