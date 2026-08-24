package com.library.user_service.dto;

import com.library.user_service.entity.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityRequestDto {

    private UUID userId;
    private ActionType actionType;
    private String description;
}