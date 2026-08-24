package com.library.notification_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // שולף אוטומטית את המייל n0527144459@gmail.com מתוך ה-properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * פונקציה כללית לשליחת מייל בסיסי
     * @param to כתובת המייל של הנמען
     * @param subject נושא המייל
     * @param body תוכן ההודעה
     */
    /*
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        System.out.println("המייל נשלח בהצלחה ל-" + to);
    }*/
    /**
     * פונקציה לשליחת מייל בתצורת HTML
     * מחזירה true אם השליחה הצליחה, ו-false אם נכשלה.
     *פשוט שיניתי שהפונ תחזיר אם השליחה הצליחה או נכשלה
     * וכן שיניתי שיהיה בפורמט של HTML
     */
    public boolean sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // הגדרת true בפרמטר השני מציינת שמדובר בהודעה מרובת חלקים (Multipart)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            // הגדרת true בפרמטר השני מציינת שהטקסט הוא HTML ולא Plain Text
            helper.setText(htmlBody, true);

            mailSender.send(message);
            System.out.println("מייל HTML נשלח בהצלחה ל-" + to);
            return true;
        } catch (Exception e) {
            System.err.println("שגיאה בשליחת המייל ל-" + to + ": " + e.getMessage());
            return false;
        }
    }
}
