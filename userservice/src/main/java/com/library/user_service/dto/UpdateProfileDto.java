package com.library.user_service.dto;

import lombok.Data;

@Data
public class UpdateProfileDto {
    private String fullName;
    private String phone;
    private String neighborhoodName;
}