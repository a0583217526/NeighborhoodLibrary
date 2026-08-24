package com.library.user_service.service;

import com.library.user_service.config.JwtTokenProvider;
import com.library.user_service.dto.AuthResponseDto;
import com.library.user_service.dto.CompleteGoogleRegistrationDto;
import com.library.user_service.dto.UserActivityRequestDto;
import com.library.user_service.entity.ActionType;
import com.library.user_service.entity.User;
import com.library.user_service.entity.Neighborhood;
import com.library.user_service.entity.UserStatus;
import com.library.user_service.repository.UserRepository;
import com.library.user_service.repository.NeighborhoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserOnboardingService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final NeighborhoodRepository neighborhoodRepository;
    private final ActivityService activityService;

    public AuthResponseDto completeGoogleRegistration(CompleteGoogleRegistrationDto dto) {
        // 1. מציאת המשתמש לפי האימייל
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("משתמש לא נמצא"));

        // 2. עדכון הטלפון
        user.setPhone(dto.getPhone());

        // 3. טיפול בשכונה: חיפוש קיימת או יצירת חדשה
        // אם לא נמצאה ב-DB, ה-orElseGet יוצר אובייקט חדש ושומר אותו
        Neighborhood neighborhood = neighborhoodRepository.findByName(dto.getNeighborhood())
                .orElseGet(() -> {
                    Neighborhood newNeighborhood = new Neighborhood();
                    newNeighborhood.setName(dto.getNeighborhood());
                    return neighborhoodRepository.save(newNeighborhood);
                });

        // 4. שיוך השכונה למשתמש
        user.setNeighborhood(neighborhood);

        user.setStatus(UserStatus.ACTIVE);

        // 5. שמירת המשתמש המעודכן
        userRepository.save(user);

        // 6. הנפקת טוקן וחזרה למשתמש
        String token = jwtTokenProvider.generateToken(user.getEmail());

        UserActivityRequestDto activityRequest = new UserActivityRequestDto();
        activityRequest.setUserId(user.getId());
        activityRequest.setActionType(ActionType.USER_REGISTER);
        activityRequest.setDescription("משתמש חדש נרשם למערכת בהצלחה באמצעות גוגל ושויך לשכונת " + neighborhood.getName());

        activityService.saveActivity(activityRequest);

        return new AuthResponseDto(user.getId(), token, user.getEmail(), user.getFullName());
    }
}