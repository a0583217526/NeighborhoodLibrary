package com.library.user_service.controller;

import com.library.user_service.dto.AuthResponseDto;
import com.library.user_service.service.GoogleAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/auth/google")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    public GoogleAuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @GetMapping("/success")
    public void handleLoginSuccess(
            @AuthenticationPrincipal OAuth2User principal,
            HttpServletResponse response
    ) throws IOException {

        String email = principal.getAttribute("email");
        String fullName = principal.getAttribute("name");

        AuthResponseDto authResponse =
                googleAuthService.processGoogleUser(email, fullName);

        String encodedId = authResponse.getId() != null ? URLEncoder.encode(authResponse.getId().toString(), StandardCharsets.UTF_8) : "";
        String encodedEmail = URLEncoder.encode(authResponse.getEmail() != null ? authResponse.getEmail() : email, StandardCharsets.UTF_8);
        String encodedFullName = URLEncoder.encode(authResponse.getFullName() != null ? authResponse.getFullName() : fullName, StandardCharsets.UTF_8);

        if (authResponse.getToken() != null) {
            String encodedToken = URLEncoder.encode(authResponse.getToken(), StandardCharsets.UTF_8);

            response.sendRedirect(
                    "http://localhost:3000/auth-success"
                            + "?id=" + encodedId
                            + "&token=" + encodedToken
                            + "&email=" + encodedEmail
                            + "&fullName=" + encodedFullName
            );
            return;
        }

        response.sendRedirect(
                "http://localhost:3000/complete-google"
                        + "?email=" + encodedEmail
                        + "&fullName=" + encodedFullName
        );
    }
}