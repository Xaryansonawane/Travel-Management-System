package com.travel.management.service;

import com.travel.management.dto.PageResponse;
import com.travel.management.dto.UserRequestDTO;
import com.travel.management.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO registerUser(UserRequestDTO requestDTO);

    UserResponseDTO getUserById(Long id);

    PageResponse<UserResponseDTO> getAllUsers(int page, int size, String sortBy, String sortDir);

    UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO);

    void deleteUser(Long id);
}
