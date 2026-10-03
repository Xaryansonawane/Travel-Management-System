package com.travel.management.dto;

import com.travel.management.enums.PackageStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response representation of a Travel Package")
public class TravelPackageResponseDTO {

    @Schema(description = "Travel package ID", example = "1")
    private Long id;

    @Schema(description = "Package name", example = "Exotic Bali 7-Day Adventure")
    private String packageName;

    @Schema(description = "Associated destination ID", example = "1")
    private Long destinationId;

    @Schema(description = "Associated destination name", example = "Bali")
    private String destinationName;

    @Schema(description = "Associated destination country", example = "Indonesia")
    private String destinationCountry;

    @Schema(description = "Package description")
    private String description;

    @Schema(description = "Duration in days", example = "7")
    private Integer duration;

    @Schema(description = "Package price per traveler", example = "899.99")
    private BigDecimal price;

    @Schema(description = "Remaining available seats", example = "25")
    private Integer availableSeats;

    @Schema(description = "Tour start date", example = "2026-11-01")
    private LocalDate startDate;

    @Schema(description = "Tour end date", example = "2026-11-08")
    private LocalDate endDate;

    @Schema(description = "Package inclusions")
    private String inclusions;

    @Schema(description = "Package exclusions")
    private String exclusions;

    @Schema(description = "Package availability status", example = "ACTIVE")
    private PackageStatus status;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
