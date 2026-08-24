package com.library.user_service.service;

import com.library.user_service.config.JwtTokenProvider;
import com.library.user_service.dto.AuthResponseDto;
import com.library.user_service.dto.RegisterUserDto;
import com.library.user_service.dto.UserActivityRequestDto;
import com.library.user_service.entity.ActionType;
import com.library.user_service.entity.Neighborhood;
import com.library.user_service.entity.User;
import com.library.user_service.repository.NeighborhoodRepository;
import com.library.user_service.repository.UserRepository;
import com.library.user_service.utils.ConversionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ActivityService activityService;

    @Transactional
    public AuthResponseDto registerUser(RegisterUserDto userDto) {

        if(userRepository.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("Email is already registered!");
        }

        Neighborhood neighborhood = neighborhoodRepository.findByName(userDto.getNeighborhoodName())
                .orElseGet(()-> {
                    Neighborhood newNeighborhood = new Neighborhood();
                    newNeighborhood.setName(userDto.getNeighborhoodName());
                    return neighborhoodRepository.save(newNeighborhood);
                });
        User user= ConversionUtil.toUser(userDto);
        user.setNeighborhood(neighborhood);

        String hashedPassword=passwordEncoder.encode(userDto.getPassword());
        user.setPassword(hashedPassword);

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail());

        UserActivityRequestDto activityRequest = new UserActivityRequestDto();
        activityRequest.setUserId(user.getId());
        activityRequest.setActionType(ActionType.USER_REGISTER);
        activityRequest.setDescription("משתמש חדש נרשם למערכת בהצלחה ושויך לשכונת " + neighborhood.getName());

        activityService.saveActivity(activityRequest);
        
        return new AuthResponseDto(user.getId(),token, user.getEmail(), user.getFullName());
    }
}
