package com.library.notification_service.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemNotificationDTO {

    private UUID toUserId;
    // לאימות משתמש שעוד לא קיים במערכת
    private String recipientEmail;

    private List<AdditionalDetailDTO> additionalDetails;
}
