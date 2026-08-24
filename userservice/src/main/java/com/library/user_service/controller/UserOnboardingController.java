package com.library.user_service.controller;

import com.library.user_service.dto.AuthResponseDto;
import com.library.user_service.dto.CompleteGoogleRegistrationDto;
import com.library.user_service.service.UserOnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class UserOnboardingController {

    private final UserOnboardingService userOnboardingService;

    /**
     * Endpoint להשלמת רישום משתמשים שהגיעו מגוגל
     * המשתמש שולח טלפון ושכונה, ואנחנו מנפיקים טוקן גישה
     */
    @PostMapping("/complete-google")
    public ResponseEntity<AuthResponseDto> completeGoogleRegistration(@RequestBody CompleteGoogleRegistrationDto dto) {
        AuthResponseDto response = userOnboardingService.completeGoogleRegistration(dto);
        return ResponseEntity.ok(response);
    }
}