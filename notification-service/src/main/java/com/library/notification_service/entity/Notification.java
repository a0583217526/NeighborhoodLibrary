package com.library.notification_service.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "NOTIFICATION")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // PK

    @ManyToOne
    @JoinColumn(name = "FromUser")
    private UserNotification fromUser; // Nullable for system messages

    @ManyToOne
    @JoinColumn(name = "ToUser", nullable = true)
    private UserNotification toUser;

    @Column(name = "Status", length = 20)
    private String status; // PENDING, SENT, FAILED

    @ManyToOne
    @JoinColumn(name = "Content", nullable = false)
    private Content content;

    @Column(name = "SendDate")
    private LocalDateTime sendDate;

    @Column(name = "RetryCount")
    private Integer retryCount = 0;

    @Column(name = "IsRead", nullable = false)
    private boolean isRead = false;

    @Column(name="RequiredSendDate", nullable = true)
    private LocalDateTime requiredSendDate;
}
