package com.library.notification_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "User_notification")
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // ה-PK העוגן של המערכת שלך

    @Column(name = "UserId", unique = true)
    private UUID userId; // המזהה החיצוני (יכול להיות NULL לאורחים)

    @Column(name = "Name", length = 150)
    private String name;

    @Column(name = "Email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "Phone", length = 20)
    private String phone;
}