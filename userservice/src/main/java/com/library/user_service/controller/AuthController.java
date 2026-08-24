package com.library.user_service.controller;

import com.library.user_service.dto.AuthResponseDto;
import com.library.user_service.dto.LoginRequestDto;
import com.library.user_service.dto.RegisterUserDto;
import com.library.user_service.service.LoginService;
import com.library.user_service.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegistrationService registrationService;
    private final LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody RegisterUserDto userDto) {
        return ResponseEntity.ok(registrationService.registerUser(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginDto) {
        return ResponseEntity.ok(loginService.login(loginDto));
    }
}