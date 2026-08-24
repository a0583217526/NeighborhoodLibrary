package com.library.notification_service.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class UserNotificationDTO {

    private UUID id;

    private String name;

    private String email;

    private String phone;
}
