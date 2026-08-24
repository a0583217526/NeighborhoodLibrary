package com.library.notification_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "NOTIFICATION_DETAILS_LINK")
public class NotificationDetailsLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // PK

    @ManyToOne
    @JoinColumn(name = "IdNotification", nullable = false)
    private Notification notification;

    @ManyToOne
    @JoinColumn(name = "IdType", nullable = false)
    private TypeAdditionalDetails type;

    @Column(name = "ContentType", length = 255)
    private String contentType; // הערך בפועל (למשל: "15/10/2026")

}