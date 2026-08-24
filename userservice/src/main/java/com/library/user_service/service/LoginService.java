package com.library.user_service.service;

import com.library.user_service.config.JwtTokenProvider;
import com.library.user_service.dto.AuthResponseDto;
import com.library.user_service.dto.LoginRequestDto;
import com.library.user_service.dto.UserActivityRequestDto;
import com.library.user_service.entity.ActionType;
import com.library.user_service.entity.User;
import com.library.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ActivityService activityService;

    @Transactional
    public AuthResponseDto login(LoginRequestDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());

        UserActivityRequestDto activityRequest = new UserActivityRequestDto();
        activityRequest.setUserId(user.getId());
        activityRequest.setActionType(ActionType.USER_LOGIN);
        activityRequest.setDescription("המשתמש התחבר למערכת בהצלחה");

        activityService.saveActivity(activityRequest);
        return new AuthResponseDto(user.getId(), token, user.getEmail(), user.getFullName());
    }
}