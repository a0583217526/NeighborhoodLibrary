package com.library.user_service.service;

import com.library.user_service.dto.UserActivityRequestDto;
import com.library.user_service.entity.ActionType;
import com.library.user_service.entity.AuthProvider;
import com.library.user_service.entity.User;
import com.library.user_service.entity.UserStatus;
import com.library.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.library.user_service.dto.AuthResponseDto;
import com.library.user_service.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ActivityService activityService;

    public AuthResponseDto processGoogleUser(String email, String fullName) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // אם המשתמש עדיין לא סיים הרשמה
            if (user.getStatus() == UserStatus.PENDING) {
                return new AuthResponseDto(user.getId(), null, email, user.getFullName());
            }

            String token = jwtTokenProvider.generateToken(email);

            UserActivityRequestDto activityRequest = new UserActivityRequestDto();
            activityRequest.setUserId(user.getId());
            activityRequest.setActionType(ActionType.USER_LOGIN);
            activityRequest.setDescription("משתמש התחבר למערכת בהצלחה באמצעות גוגל");

            activityService.saveActivity(activityRequest);
            return new AuthResponseDto(user.getId(), token, email, user.getFullName());

        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(fullName);

            newUser.setAuthProvider(AuthProvider.GOOGLE);
            newUser.setStatus(UserStatus.PENDING);

            userRepository.save(newUser);

            return new AuthResponseDto(newUser.getId(), null, email, fullName);
        }
    }
}