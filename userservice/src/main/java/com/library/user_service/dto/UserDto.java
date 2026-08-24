package com.library.user_service.dto;

import com.library.user_service.entity.AuthProvider;
import com.library.user_service.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;
    private String fullName;
    private String email;
    private String phone;

    private Long neighborhoodId;
    private String neighborhoodName;

    private AuthProvider authProvider;
    private UserStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
