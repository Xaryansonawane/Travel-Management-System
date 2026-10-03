package com.travel.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for making a new booking")
public class BookingRequestDTO {

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user making the booking", example = "1")
    private Long userId;

    @NotNull(message = "Travel Package ID is required")
    @Schema(description = "ID of the travel package to book", example = "1")
    private Long travelPackageId;

    @NotNull(message = "Number of travelers is required")
    @Min(value = 1, message = "At least 1 traveler is required")
    @Schema(description = "Total number of traveler seats requested", example = "2")
    private Integer numberOfTravelers;
}
