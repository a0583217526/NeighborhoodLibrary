package com.library.user_service.controller;

import com.library.user_service.dto.ActivityLogDto;
import com.library.user_service.dto.UserActivityRequestDto;
import com.library.user_service.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class UserActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<Void> saveActivity(@RequestBody UserActivityRequestDto request) {
        activityService.saveActivity(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<ActivityLogDto>> getUserActivityLog(Authentication authentication) {
        return ResponseEntity.ok(activityService.getUserActivityLog(authentication.getName()));
    }
}