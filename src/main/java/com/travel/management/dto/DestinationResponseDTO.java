package com.travel.management.dto;

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
@Schema(description = "Response representation of a Destination")
public class DestinationResponseDTO {

    @Schema(description = "Destination unique ID", example = "1")
    private Long id;

    @Schema(description = "Destination name", example = "Bali")
    private String name;

    @Schema(description = "Country", example = "Indonesia")
    private String country;

    @Schema(description = "State/Region", example = "Bali Province")
    private String state;

    @Schema(description = "Destination description")
    private String description;

    @Schema(description = "Recommended months/season to visit", example = "April to October")
    private String bestTimeToVisit;

    @Schema(description = "Destination image URL")
    private String imageUrl;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}
