package com.library.notification_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CONTENT")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // PK

    @Column(name = "Subject", length = 255)
    private String subject;

    @Column(name = "ContentNotification", columnDefinition = "TEXT")
    private String contentNotification;
}