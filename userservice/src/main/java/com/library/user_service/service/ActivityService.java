package com.library.user_service.service;

import com.library.user_service.dto.ActivityLogDto;
import com.library.user_service.dto.UserActivityRequestDto;
import com.library.user_service.entity.ActionType;
import com.library.user_service.entity.ActivityLog;
import com.library.user_service.entity.User;
import com.library.user_service.repository.ActivityLogRepository;
import com.library.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveActivity(UserActivityRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setActionType(request.getActionType());
        log.setDescription(request.getDescription());

        activityLogRepository.save(log);
    }

    public List<ActivityLogDto> getUserActivityLog(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "משתמש לא נמצא"));

        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(log -> new ActivityLogDto(
                        log.getActionType(),
                        getHebrewTranslation(log.getActionType()),
                        log.getDescription(),
                        log.getCreatedAt()
                ))
                .toList();
    }

    private String getHebrewTranslation(ActionType actionType) {
        if (actionType == null) return "פעילות כללית";
        return switch (actionType) {
            case USER_REGISTER -> "רישום למערכת";
            case USER_LOGIN -> "התחברות למערכת";
            case USER_UPDATE_PROFILE -> "עדכון פרופיל";
            case USER_DEACTIVATE -> "הקפאת חשבון";
            case BOOK_ADDED -> "הוספת ספר חדש";
            case BOOK_UPDATED -> "עדכון פרטי ספר";
            case BOOK_REMOVED -> "מחיקת ספר";
            case LOAN_REQUEST_CREATED -> "בקשת השאלה חדשה";
            case LOAN_REQUEST_APPROVED -> "אישור בקשת השאלה";
            case LOAN_REQUEST_REJECTED -> "דחיית בקשת השאלה";
            case LOAN_BOOK_RETURNED -> "החזרת ספר";
            case LOAN_OVERDUE -> "ספר באיחור";
            default -> "פעילות כללית";
        };
    }
}