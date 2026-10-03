package com.travel.management.mapper;

import com.travel.management.dto.UserRequestDTO;
import com.travel.management.dto.UserResponseDTO;
import com.travel.management.entity.User;
import com.travel.management.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .name(dto.getName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .phone(dto.getPhone().trim())
                .password(dto.getPassword())
                .role(dto.getRole() != null ? dto.getRole() : UserRole.USER)
                .build();
    }

    public UserResponseDTO toDTO(User entity) {
        if (entity == null) {
            return null;
        }
        return UserResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(UserRequestDTO dto, User entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setName(dto.getName().trim());
        entity.setEmail(dto.getEmail().trim().toLowerCase());
        entity.setPhone(dto.getPhone().trim());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(dto.getPassword());
        }
        if (dto.getRole() != null) {
            entity.setRole(dto.getRole());
        }
    }
}
