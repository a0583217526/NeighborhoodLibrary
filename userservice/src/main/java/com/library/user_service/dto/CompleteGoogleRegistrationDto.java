package com.library.user_service.dto;

import lombok.Data;

@Data
public class CompleteGoogleRegistrationDto {
    // השדות האלו באים מגוגל (הפרונטאנד שולח אותם חזרה)
    private String email;
    private String fullName;

    // השדות האלו הם מה שהמשתמש מזין בטופס
    private String phone;
    private String neighborhood;
}