package com.travel.management.dto;

import com.travel.management.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response representation of a User")
public class UserResponseDTO {

    @Schema(description = "User unique ID", example = "1")
    private Long id;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String name;

    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User phone number", example = "+1-555-0199")
    private String phone;

    @Schema(description = "Assigned user role", example = "USER")
    private UserRole role;

    @Schema(description = "Timestamp when the user was registered")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the user record was last updated")
    private LocalDateTime updatedAt;
}
