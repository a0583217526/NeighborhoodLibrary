package com.library.notification_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "TYPE_ADDITIONAL_DETAILS")
public class TypeAdditionalDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // PK

    @Column(name = "TypeDetail", length = 100)
    private String typeDetail;
}