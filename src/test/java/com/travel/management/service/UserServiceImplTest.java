package com.travel.management.service;

import com.travel.management.dto.PageResponse;
import com.travel.management.dto.UserRequestDTO;
import com.travel.management.dto.UserResponseDTO;
import com.travel.management.entity.User;
import com.travel.management.enums.UserRole;
import com.travel.management.exception.DuplicateResourceException;
import com.travel.management.exception.ResourceNotFoundException;
import com.travel.management.mapper.UserMapper;
import com.travel.management.repository.UserRepository;
import com.travel.management.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+1-555-0199")
                .password("Password@123")
                .role(UserRole.USER)
                .build();

        userRequestDTO = UserRequestDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+1-555-0199")
                .password("Password@123")
                .role(UserRole.USER)
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+1-555-0199")
                .role(UserRole.USER)
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void registerUser_Success() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(userMapper.toEntity(userRequestDTO)).thenReturn(sampleUser);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toDTO(sampleUser)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.registerUser(userRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when registering with an existing email")
    void registerUser_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return user when found by ID")
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toDTO(sampleUser)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user is not found by ID")
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    @DisplayName("Should successfully get all users paginated")
    void getAllUsers_Success() {
        Page<User> pagedUsers = new PageImpl<>(List.of(sampleUser));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(pagedUsers);
        when(userMapper.toDTO(sampleUser)).thenReturn(userResponseDTO);

        PageResponse<UserResponseDTO> result = userService.getAllUsers(0, 10, "id", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should successfully update existing user")
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toDTO(sampleUser)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.updateUser(1L, userRequestDTO);

        assertThat(result).isNotNull();
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("Should successfully delete existing user")
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(sampleUser);
    }
}
