package com.travel.management.repository;

import com.travel.management.entity.Booking;
import com.travel.management.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Page<Booking> findByTravelPackageId(Long packageId, Pageable pageable);

    Page<Booking> findByBookingStatus(BookingStatus bookingStatus, Pageable pageable);
}
