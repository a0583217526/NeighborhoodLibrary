package com.library.user_service.utils;

import com.library.user_service.dto.RegisterUserDto;
import com.library.user_service.dto.UserDto;
import com.library.user_service.entity.User;

public class ConversionUtil {
    public static User toUser(RegisterUserDto userDto)
    {
        User user=new User();
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        return user;
    }

    public static UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAuthProvider(user.getAuthProvider());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        if (user.getNeighborhood() != null) {
            dto.setNeighborhoodId(user.getNeighborhood().getId());
            dto.setNeighborhoodName(user.getNeighborhood().getName());
        }

        return dto;
    }
}