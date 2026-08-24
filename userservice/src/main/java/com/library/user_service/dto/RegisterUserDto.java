package com.library.user_service.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserDto {
    @NotBlank(message = "שם מלא הוא שדה חובה")
    @Size(max = 100, message = "שם מלא לא יכול לעלות על 100 תווים")
    private String fullName;

    @NotBlank(message = "אימייל הוא שדה חובה")
    @Email(message = "כתובת אימייל לא תקינה")
    @Size(max = 150, message = "אימייל לא יכול לעלות על 150 תווים")
    private String email;

    @NotBlank(message = "סיסמה היא שדה חובה")
    @Size(min = 6, max = 255, message = "הסיסמה חייבת להיות בין 6 ל-255 תווים")
    private String password;

    @NotBlank(message = "מספר טלפון הוא שדה חובה")
    @Size(max = 20, message = "מספר טלפון לא תקין")
    private String phone;

    @NotBlank(message = "שם שכונה הוא שדה חובה")
    private String neighborhoodName;
}
