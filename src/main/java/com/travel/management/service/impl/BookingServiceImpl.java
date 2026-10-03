package com.travel.management.service.impl;

import com.travel.management.dto.BookingRequestDTO;
import com.travel.management.dto.BookingResponseDTO;
import com.travel.management.dto.BookingStatusUpdateDTO;
import com.travel.management.dto.PageResponse;
import com.travel.management.entity.Booking;
import com.travel.management.entity.TravelPackage;
import com.travel.management.entity.User;
import com.travel.management.enums.BookingStatus;
import com.travel.management.enums.PackageStatus;
import com.travel.management.enums.PaymentStatus;
import com.travel.management.exception.InsufficientSeatsException;
import com.travel.management.exception.InvalidBookingException;
import com.travel.management.exception.ResourceNotFoundException;
import com.travel.management.mapper.BookingMapper;
import com.travel.management.repository.BookingRepository;
import com.travel.management.repository.TravelPackageRepository;
import com.travel.management.repository.UserRepository;
import com.travel.management.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TravelPackageRepository packageRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO requestDTO) {
        log.info("Processing booking request: user ID {}, package ID {}, travelers {}",
                requestDTO.getUserId(), requestDTO.getTravelPackageId(), requestDTO.getNumberOfTravelers());

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDTO.getUserId()));

        TravelPackage travelPackage = packageRepository.findById(requestDTO.getTravelPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("TravelPackage", "id", requestDTO.getTravelPackageId()));

        // Invariant check: Package status
        if (travelPackage.getStatus() == PackageStatus.INACTIVE) {
            log.warn("Booking rejected: Package ID {} is INACTIVE", travelPackage.getId());
            throw new InvalidBookingException("Cannot book package: Package is currently INACTIVE");
        }

        if (travelPackage.getStatus() == PackageStatus.SOLD_OUT || travelPackage.getAvailableSeats() <= 0) {
            log.warn("Booking rejected: Package ID {} is SOLD_OUT", travelPackage.getId());
            throw new InvalidBookingException("Cannot book package: Package is SOLD_OUT");
        }

        // Invariant check: Departure date has not passed
        if (travelPackage.getStartDate().isBefore(LocalDate.now())) {
            log.warn("Booking rejected: Departure date {} for package ID {} has already passed",
                    travelPackage.getStartDate(), travelPackage.getId());
            throw new InvalidBookingException("Cannot book package: Departure date has already passed (" + travelPackage.getStartDate() + ")");
        }

        // Invariant check: Available seats sufficiency
        int requestedTravelers = requestDTO.getNumberOfTravelers();
        if (travelPackage.getAvailableSeats() < requestedTravelers) {
            log.warn("Booking rejected: Insufficient seats. Requested: {}, Available: {}",
                    requestedTravelers, travelPackage.getAvailableSeats());
            throw new InsufficientSeatsException(requestedTravelers, travelPackage.getAvailableSeats());
        }

        // Calculate total amount = unit price * travelers
        BigDecimal totalAmount = travelPackage.getPrice().multiply(BigDecimal.valueOf(requestedTravelers));

        // Atomic seat reduction
        int remainingSeats = travelPackage.getAvailableSeats() - requestedTravelers;
        travelPackage.setAvailableSeats(remainingSeats);
        if (remainingSeats == 0) {
            travelPackage.setStatus(PackageStatus.SOLD_OUT);
            log.info("Package ID {} is now SOLD_OUT", travelPackage.getId());
        }
        packageRepository.save(travelPackage);

        // Create booking record
        Booking booking = Booking.builder()
                .user(user)
                .travelPackage(travelPackage)
                .bookingDate(LocalDateTime.now())
                .numberOfTravelers(requestedTravelers)
                .totalAmount(totalAmount)
                .bookingStatus(BookingStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {}, Total Amount: {}", saved.getId(), totalAmount);
        return bookingMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingById(Long id) {
        log.info("Fetching booking details for ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        return bookingMapper.toDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponseDTO> getAllBookings(int page, int size, String sortBy, String sortDir) {
        log.info("Fetching all bookings - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Booking> pageResult = bookingRepository.findAll(pageable);
        return PageResponse.from(pageResult.map(bookingMapper::toDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponseDTO> getBookingsByUser(Long userId, int page, int size, String sortBy, String sortDir) {
        log.info("Fetching bookings for user ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Booking> results = bookingRepository.findByUserId(userId, pageable);
        return PageResponse.from(results.map(bookingMapper::toDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponseDTO> getBookingsByPackage(Long packageId, int page, int size, String sortBy, String sortDir) {
        log.info("Fetching bookings for package ID: {}", packageId);
        if (!packageRepository.existsById(packageId)) {
            throw new ResourceNotFoundException("TravelPackage", "id", packageId);
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Booking> results = bookingRepository.findByTravelPackageId(packageId, pageable);
        return PageResponse.from(results.map(bookingMapper::toDTO));
    }

    @Override
    public BookingResponseDTO updateBooking(Long id, BookingStatusUpdateDTO updateDTO) {
        log.info("Updating booking ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        TravelPackage travelPackage = booking.getTravelPackage();

        // Check if number of travelers is being modified
        if (updateDTO.getNumberOfTravelers() != null &&
                !updateDTO.getNumberOfTravelers().equals(booking.getNumberOfTravelers())) {

            if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
                throw new InvalidBookingException("Cannot modify travelers for a cancelled booking");
            }

            int oldTravelers = booking.getNumberOfTravelers();
            int newTravelers = updateDTO.getNumberOfTravelers();
            int difference = newTravelers - oldTravelers;

            if (difference > 0) {
                // More seats requested
                if (travelPackage.getAvailableSeats() < difference) {
                    throw new InsufficientSeatsException(difference, travelPackage.getAvailableSeats());
                }
                travelPackage.setAvailableSeats(travelPackage.getAvailableSeats() - difference);
                if (travelPackage.getAvailableSeats() == 0) {
                    travelPackage.setStatus(PackageStatus.SOLD_OUT);
                }
            } else {
                // Fewer seats requested (restore seats)
                int seatsToRestore = Math.abs(difference);
                travelPackage.setAvailableSeats(travelPackage.getAvailableSeats() + seatsToRestore);
                if (travelPackage.getStatus() == PackageStatus.SOLD_OUT && travelPackage.getAvailableSeats() > 0) {
                    travelPackage.setStatus(PackageStatus.ACTIVE);
                }
            }

            booking.setNumberOfTravelers(newTravelers);
            booking.setTotalAmount(travelPackage.getPrice().multiply(BigDecimal.valueOf(newTravelers)));
            packageRepository.save(travelPackage);
        }

        // Check status change to CANCELLED
        if (updateDTO.getBookingStatus() != null) {
            if (updateDTO.getBookingStatus() == BookingStatus.CANCELLED && booking.getBookingStatus() != BookingStatus.CANCELLED) {
                // Restore seats upon transition to CANCELLED
                travelPackage.setAvailableSeats(travelPackage.getAvailableSeats() + booking.getNumberOfTravelers());
                if (travelPackage.getStatus() == PackageStatus.SOLD_OUT && travelPackage.getAvailableSeats() > 0) {
                    travelPackage.setStatus(PackageStatus.ACTIVE);
                }
                packageRepository.save(travelPackage);
            }
            booking.setBookingStatus(updateDTO.getBookingStatus());
        }

        // Check payment status update
        if (updateDTO.getPaymentStatus() != null) {
            booking.setPaymentStatus(updateDTO.getPaymentStatus());
        }

        Booking updated = bookingRepository.save(booking);
        log.info("Booking ID {} updated successfully", id);
        return bookingMapper.toDTO(updated);
    }

    @Override
    public BookingResponseDTO cancelBooking(Long id) {
        log.info("Processing cancellation for booking ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            log.warn("Cancellation rejected: Booking ID {} is already CANCELLED", id);
            throw new InvalidBookingException("Booking is already cancelled");
        }

        TravelPackage travelPackage = booking.getTravelPackage();

        // Restore seats to the travel package
        int restoredSeats = travelPackage.getAvailableSeats() + booking.getNumberOfTravelers();
        travelPackage.setAvailableSeats(restoredSeats);

        // If package was SOLD_OUT, reactive it
        if (travelPackage.getStatus() == PackageStatus.SOLD_OUT && restoredSeats > 0) {
            travelPackage.setStatus(PackageStatus.ACTIVE);
            log.info("Package ID {} transitioned from SOLD_OUT to ACTIVE following cancellation", travelPackage.getId());
        }
        packageRepository.save(travelPackage);

        // Update booking and payment statuses
        booking.setBookingStatus(BookingStatus.CANCELLED);
        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            booking.setPaymentStatus(PaymentStatus.REFUNDED);
            log.info("Payment for booking ID {} marked as REFUNDED", id);
        }

        Booking updated = bookingRepository.save(booking);
        log.info("Booking ID {} cancelled successfully; restored {} seats", id, booking.getNumberOfTravelers());
        return bookingMapper.toDTO(updated);
    }
}
