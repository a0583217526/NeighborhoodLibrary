package com.library.user_service.dto;

import com.library.user_service.entity.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDto {
    private ActionType actionType;
    private String actionTypeHebrew;
    private String description;
    private LocalDateTime createdAt;
}