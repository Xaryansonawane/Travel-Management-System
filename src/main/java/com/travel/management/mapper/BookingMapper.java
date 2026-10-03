package com.travel.management.mapper;

import com.travel.management.dto.BookingResponseDTO;
import com.travel.management.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponseDTO toDTO(Booking entity) {
        if (entity == null) {
            return null;
        }

        Long userId = entity.getUser() != null ? entity.getUser().getId() : null;
        String userName = entity.getUser() != null ? entity.getUser().getName() : null;
        String userEmail = entity.getUser() != null ? entity.getUser().getEmail() : null;

        Long packageId = entity.getTravelPackage() != null ? entity.getTravelPackage().getId() : null;
        String packageName = entity.getTravelPackage() != null ? entity.getTravelPackage().getPackageName() : null;
        String destinationName = (entity.getTravelPackage() != null && entity.getTravelPackage().getDestination() != null)
                ? entity.getTravelPackage().getDestination().getName()
                : null;
        java.math.BigDecimal packagePrice = entity.getTravelPackage() != null ? entity.getTravelPackage().getPrice() : null;

        return BookingResponseDTO.builder()
                .id(entity.getId())
                .userId(userId)
                .userName(userName)
                .userEmail(userEmail)
                .travelPackageId(packageId)
                .packageName(packageName)
                .destinationName(destinationName)
                .bookingDate(entity.getBookingDate())
                .numberOfTravelers(entity.getNumberOfTravelers())
                .packagePrice(packagePrice)
                .totalAmount(entity.getTotalAmount())
                .bookingStatus(entity.getBookingStatus())
                .paymentStatus(entity.getPaymentStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
