package com.library.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private UUID id;
    private String token;
    private String email;
    private String fullName;
}

