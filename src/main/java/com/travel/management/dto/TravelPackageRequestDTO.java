package com.travel.management.dto;

import com.travel.management.enums.PackageStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating or updating a Travel Package")
public class TravelPackageRequestDTO {

    @NotBlank(message = "Package name is required")
    @Schema(description = "Title of the travel package", example = "Exotic Bali 7-Day Adventure")
    private String packageName;

    @NotNull(message = "Destination ID is required")
    @Schema(description = "ID of the associated destination", example = "1")
    private Long destinationId;

    @Schema(description = "Detailed package itinerary and description", example = "Experience the best of Bali with private tours, temple visits, and beachside luxury.")
    private String description;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than 0 days")
    @Schema(description = "Duration of the package in days", example = "7")
    private Integer duration;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    @Schema(description = "Price per traveler in USD", example = "899.99")
    private BigDecimal price;

    @NotNull(message = "Available seats is required")
    @Min(value = 0, message = "Available seats cannot be negative")
    @Schema(description = "Total number of available bookable seats", example = "25")
    private Integer availableSeats;

    @NotNull(message = "Start date is required")
    @Schema(description = "Departure/start date (YYYY-MM-DD)", example = "2026-11-01")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "Return/end date (YYYY-MM-DD)", example = "2026-11-08")
    private LocalDate endDate;

    @Schema(description = "Items included in package", example = "4-Star Hotel, Breakfast, Guided Tours, Airport Transfers")
    private String inclusions;

    @Schema(description = "Items excluded from package", example = "International Flights, Travel Insurance, Personal Expenses")
    private String exclusions;

    @Schema(description = "Package status (ACTIVE, INACTIVE, SOLD_OUT)", example = "ACTIVE", defaultValue = "ACTIVE")
    private PackageStatus status;
}
