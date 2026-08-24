package com.library.user_service.service;

import com.library.user_service.dto.UpdateProfileDto;
import com.library.user_service.dto.UserActivityRequestDto;
import com.library.user_service.dto.UserDto;
import com.library.user_service.entity.ActionType;
import com.library.user_service.entity.Neighborhood;
import com.library.user_service.entity.User;
import com.library.user_service.repository.NeighborhoodRepository;
import com.library.user_service.repository.UserRepository;
import com.library.user_service.utils.ConversionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final ActivityService activityService;

    public UserDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "משתמש לא נמצא"));
        return ConversionUtil.toUserDto(user);
    }

    @Transactional
    public UserDto updateUserProfile(String email, UpdateProfileDto updateDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "משתמש לא נמצא"));

        if (updateDto.getFullName() != null && !updateDto.getFullName().trim().isEmpty()) {
            user.setFullName(updateDto.getFullName());
        }
        if (updateDto.getPhone() != null && !updateDto.getPhone().trim().isEmpty()) {
            user.setPhone(updateDto.getPhone());
        }
        if (updateDto.getNeighborhoodName() != null && !updateDto.getNeighborhoodName().trim().isEmpty()) {
            String neighborhoodName = updateDto.getNeighborhoodName();
            Neighborhood neighborhood = neighborhoodRepository.findByName(neighborhoodName)
                    .orElseGet(() -> {
                        Neighborhood newNeighborhood = new Neighborhood();
                        newNeighborhood.setName(neighborhoodName);
                        return neighborhoodRepository.save(newNeighborhood);
                    });
            user.setNeighborhood(neighborhood);
        }

        User updatedUser = userRepository.save(user);

        UserActivityRequestDto activityRequest = new UserActivityRequestDto();
        activityRequest.setUserId(user.getId());
        activityRequest.setActionType(ActionType.USER_UPDATE_PROFILE);
        activityRequest.setDescription("המשתמש עדכן את פרטי הפרופיל שלו בהצלחה");

        activityService.saveActivity(activityRequest);

        return ConversionUtil.toUserDto(updatedUser);
    }
}