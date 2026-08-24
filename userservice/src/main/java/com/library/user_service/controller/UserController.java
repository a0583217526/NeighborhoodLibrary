package com.library.user_service.controller;

import com.library.user_service.dto.UpdateProfileDto;
import com.library.user_service.dto.UserDto;
import com.library.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserProfile(authentication.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(Authentication authentication,
                                                 @RequestBody UpdateProfileDto updateProfileDto) {
        return ResponseEntity.ok(userService.updateUserProfile(authentication.getName(), updateProfileDto));
    }
}