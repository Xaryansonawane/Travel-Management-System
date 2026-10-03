package com.travel.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating or updating a Destination")
public class DestinationRequestDTO {

    @NotBlank(message = "Destination name is required")
    @Size(min = 2, max = 150, message = "Destination name must be between 2 and 150 characters")
    @Schema(description = "Unique name of the destination", example = "Bali")
    private String name;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    @Schema(description = "Country where destination is located", example = "Indonesia")
    private String country;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 100, message = "State must be between 2 and 100 characters")
    @Schema(description = "State, province, or region", example = "Bali Province")
    private String state;

    @Schema(description = "Detailed destination description", example = "Tropical paradise renowned for volcanic mountains, iconic beaches, and coral reefs.")
    private String description;

    @Schema(description = "Best season or months to visit", example = "April to October")
    private String bestTimeToVisit;

    @Schema(description = "Representative image URL", example = "https://images.unsplash.com/photo-1537996194471-e657df975ab4")
    private String imageUrl;
}
