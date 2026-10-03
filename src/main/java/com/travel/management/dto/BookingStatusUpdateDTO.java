package com.travel.management.dto;

import com.travel.management.enums.BookingStatus;
import com.travel.management.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for updating booking status or payment status")
public class BookingStatusUpdateDTO {

    @Schema(description = "Updated booking status (CONFIRMED, PENDING, CANCELLED, COMPLETED)", example = "CONFIRMED")
    private BookingStatus bookingStatus;

    @Schema(description = "Updated payment status (PENDING, PAID, FAILED, REFUNDED)", example = "PAID")
    private PaymentStatus paymentStatus;

    @Schema(description = "Updated number of travelers (optional)", example = "3")
    private Integer numberOfTravelers;
}
