package com.travel.management.dto;

import com.travel.management.enums.BookingStatus;
import com.travel.management.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response representation of a Booking")
public class BookingResponseDTO {

    @Schema(description = "Unique booking ID", example = "1")
    private Long id;

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "User Name", example = "John Doe")
    private String userName;

    @Schema(description = "User Email", example = "john.doe@example.com")
    private String userEmail;

    @Schema(description = "Travel Package ID", example = "1")
    private Long travelPackageId;

    @Schema(description = "Travel Package Name", example = "Exotic Bali 7-Day Adventure")
    private String packageName;

    @Schema(description = "Destination Name", example = "Bali")
    private String destinationName;

    @Schema(description = "Date and time when booking was placed")
    private LocalDateTime bookingDate;

    @Schema(description = "Number of booked travelers", example = "2")
    private Integer numberOfTravelers;

    @Schema(description = "Package unit price per traveler", example = "899.99")
    private BigDecimal packagePrice;

    @Schema(description = "Calculated total booking amount", example = "1799.98")
    private BigDecimal totalAmount;

    @Schema(description = "Current booking status", example = "CONFIRMED")
    private BookingStatus bookingStatus;

    @Schema(description = "Current payment status", example = "PAID")
    private PaymentStatus paymentStatus;

    @Schema(description = "Booking creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
