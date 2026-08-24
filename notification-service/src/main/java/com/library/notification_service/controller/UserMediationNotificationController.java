package com.library.notification_service.controller;

import com.library.notification_service.DTO.UserToUserNotificationDTO;
import com.library.notification_service.service.UserMediationNotificationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/notifications/mediation")
public class UserMediationNotificationController {

    private final UserMediationNotificationService mediationNotificationService;

    public UserMediationNotificationController(UserMediationNotificationService mediationNotificationService) {
        this.mediationNotificationService = mediationNotificationService;
    }

    @PostMapping("/loan-request")
    public ResponseEntity<Void> triggerLoanRequest(
            @RequestBody UserToUserNotificationDTO dto  ){
        mediationNotificationService.sendLoanRequestAlert(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/loan-approval")
    public ResponseEntity<Void> triggerLoanApproval(@RequestBody UserToUserNotificationDTO dto) {
        mediationNotificationService.sendLoanApprovalNotification(dto);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/loan-extension")
    public ResponseEntity<Void> triggerLoanExtension(
            @RequestBody UserToUserNotificationDTO userToUserNotificationDTO) {
        mediationNotificationService.sendLoanExtensionRequest(userToUserNotificationDTO);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/loan-extension/approve")
    public ResponseEntity<Void> triggerApproveLoanExtension(
            @RequestBody UserToUserNotificationDTO userToUserNotificationDTO) {

        mediationNotificationService.approveLoanExtensionRequest(userToUserNotificationDTO);
        return ResponseEntity.ok().build();
    }
}