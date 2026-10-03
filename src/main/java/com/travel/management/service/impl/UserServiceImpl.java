package com.travel.management.service.impl;

import com.travel.management.dto.PageResponse;
import com.travel.management.dto.UserRequestDTO;
import com.travel.management.dto.UserResponseDTO;
import com.travel.management.entity.User;
import com.travel.management.exception.DuplicateResourceException;
import com.travel.management.exception.ResourceNotFoundException;
import com.travel.management.mapper.UserMapper;
import com.travel.management.repository.UserRepository;
import com.travel.management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO registerUser(UserRequestDTO requestDTO) {
        log.info("Attempting to register new user with email: {}", requestDTO.getEmail());
        String normalizedEmail = requestDTO.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Registration failed: User with email {} already exists", normalizedEmail);
            throw new DuplicateResourceException("User already exists with email: " + normalizedEmail);
        }

        User user = userMapper.toEntity(requestDTO);
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.info("Fetching user details for ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponseDTO> getAllUsers(int page, int size, String sortBy, String sortDir) {
        log.info("Fetching all users - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.findAll(pageable);
        Page<UserResponseDTO> dtoPage = usersPage.map(userMapper::toDTO);
        return PageResponse.from(dtoPage);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        log.info("Updating user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        String normalizedEmail = requestDTO.getEmail().trim().toLowerCase();
        if (!existingUser.getEmail().equalsIgnoreCase(normalizedEmail) && userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Update failed: Email {} is already in use by another user", normalizedEmail);
            throw new DuplicateResourceException("Email already in use by another account: " + normalizedEmail);
        }

        userMapper.updateEntityFromDTO(requestDTO, existingUser);
        User updatedUser = userRepository.save(existingUser);
        log.info("User ID {} updated successfully", id);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
        log.info("User ID {} deleted successfully", id);
    }
}
