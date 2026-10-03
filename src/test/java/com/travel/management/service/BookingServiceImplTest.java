package com.travel.management.service;

import com.travel.management.dto.BookingRequestDTO;
import com.travel.management.dto.BookingResponseDTO;
import com.travel.management.entity.Booking;
import com.travel.management.entity.Destination;
import com.travel.management.entity.TravelPackage;
import com.travel.management.entity.User;
import com.travel.management.enums.BookingStatus;
import com.travel.management.enums.PackageStatus;
import com.travel.management.enums.PaymentStatus;
import com.travel.management.enums.UserRole;
import com.travel.management.exception.InsufficientSeatsException;
import com.travel.management.exception.InvalidBookingException;
import com.travel.management.exception.ResourceNotFoundException;
import com.travel.management.mapper.BookingMapper;
import com.travel.management.repository.BookingRepository;
import com.travel.management.repository.TravelPackageRepository;
import com.travel.management.repository.UserRepository;
import com.travel.management.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TravelPackageRepository packageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User sampleUser;
    private Destination sampleDestination;
    private TravelPackage samplePackage;
    private Booking sampleBooking;
    private BookingRequestDTO bookingRequestDTO;
    private BookingResponseDTO bookingResponseDTO;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .name("Alice Johnson")
                .email("alice@travel.com")
                .phone("+1-555-0101")
                .password("Password@123")
                .role(UserRole.USER)
                .build();

        sampleDestination = Destination.builder()
                .id(1L)
                .name("Bali")
                .country("Indonesia")
                .state("Bali")
                .build();

        samplePackage = TravelPackage.builder()
                .id(1L)
                .packageName("Exotic Bali Escape")
                .destination(sampleDestination)
                .duration(7)
                .price(new BigDecimal("500.00"))
                .availableSeats(10)
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(1).plusDays(7))
                .status(PackageStatus.ACTIVE)
                .build();

        sampleBooking = Booking.builder()
                .id(1L)
                .user(sampleUser)
                .travelPackage(samplePackage)
                .bookingDate(LocalDateTime.now())
                .numberOfTravelers(2)
                .totalAmount(new BigDecimal("1000.00"))
                .bookingStatus(BookingStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        bookingRequestDTO = BookingRequestDTO.builder()
                .userId(1L)
                .travelPackageId(1L)
                .numberOfTravelers(2)
                .build();

        bookingResponseDTO = BookingResponseDTO.builder()
                .id(1L)
                .userId(1L)
                .userName("Alice Johnson")
                .travelPackageId(1L)
                .packageName("Exotic Bali Escape")
                .numberOfTravelers(2)
                .totalAmount(new BigDecimal("1000.00"))
                .bookingStatus(BookingStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a booking, reduce seats, and calculate total amount")
    void createBooking_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(packageRepository.findById(1L)).thenReturn(Optional.of(samplePackage));
        when(bookingRepository.save(any(Booking.class))).thenReturn(sampleBooking);
        when(bookingMapper.toDTO(any(Booking.class))).thenReturn(bookingResponseDTO);

        BookingResponseDTO result = bookingService.createBooking(bookingRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNumberOfTravelers()).isEqualTo(2);
        // Verify seats decremented: 10 - 2 = 8
        assertThat(samplePackage.getAvailableSeats()).isEqualTo(8);
        verify(packageRepository).save(samplePackage);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should transition package status to SOLD_OUT when available seats reach zero")
    void createBooking_TransitionToSoldOut_WhenSeatsReachZero() {
        samplePackage.setAvailableSeats(2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(packageRepository.findById(1L)).thenReturn(Optional.of(samplePackage));
        when(bookingRepository.save(any(Booking.class))).thenReturn(sampleBooking);
        when(bookingMapper.toDTO(any(Booking.class))).thenReturn(bookingResponseDTO);

        bookingService.createBooking(bookingRequestDTO);

        assertThat(samplePackage.getAvailableSeats()).isEqualTo(0);
        assertThat(samplePackage.getStatus()).isEqualTo(PackageStatus.SOLD_OUT);
        verify(packageRepository).save(samplePackage);
    }

    @Test
    @DisplayName("Should throw InsufficientSeatsException when requested seats exceed available seats")
    void createBooking_InsufficientSeats_ThrowsException() {
        bookingRequestDTO.setNumberOfTravelers(15);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(packageRepository.findById(1L)).thenReturn(Optional.of(samplePackage));

        assertThrows(InsufficientSeatsException.class, () -> bookingService.createBooking(bookingRequestDTO));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void createBooking_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(bookingRequestDTO));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when package does not exist")
    void createBooking_PackageNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(packageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(bookingRequestDTO));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when package is INACTIVE")
    void createBooking_InactivePackage_ThrowsException() {
        samplePackage.setStatus(PackageStatus.INACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(packageRepository.findById(1L)).thenReturn(Optional.of(samplePackage));

        assertThrows(InvalidBookingException.class, () -> bookingService.createBooking(bookingRequestDTO));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when package is SOLD_OUT")
    void createBooking_SoldOutPackage_ThrowsException() {
        samplePackage.setStatus(PackageStatus.SOLD_OUT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(packageRepository.findById(1L)).thenReturn(Optional.of(samplePackage));

        assertThrows(InvalidBookingException.class, () -> bookingService.createBooking(bookingRequestDTO));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when package start date is in the past")
    void createBooking_PastStartDate_ThrowsException() {
        samplePackage.setStartDate(LocalDate.now().minusDays(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(packageRepository.findById(1L)).thenReturn(Optional.of(samplePackage));

        assertThrows(InvalidBookingException.class, () -> bookingService.createBooking(bookingRequestDTO));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should cancel booking, restore available seats, and reactivate package if SOLD_OUT")
    void cancelBooking_Success_RestoresSeatsAndReactivatesPackage() {
        samplePackage.setAvailableSeats(0);
        samplePackage.setStatus(PackageStatus.SOLD_OUT);
        sampleBooking.setNumberOfTravelers(2);
        sampleBooking.setPaymentStatus(PaymentStatus.PAID);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(sampleBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(sampleBooking);
        when(bookingMapper.toDTO(any(Booking.class))).thenReturn(bookingResponseDTO);

        bookingService.cancelBooking(1L);

        // Verify seats restored: 0 + 2 = 2
        assertThat(samplePackage.getAvailableSeats()).isEqualTo(2);
        // Verify package status reactivated to ACTIVE
        assertThat(samplePackage.getStatus()).isEqualTo(PackageStatus.ACTIVE);
        // Verify booking status set to CANCELLED and payment to REFUNDED
        assertThat(sampleBooking.getBookingStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(sampleBooking.getPaymentStatus()).isEqualTo(PaymentStatus.REFUNDED);

        verify(packageRepository).save(samplePackage);
        verify(bookingRepository).save(sampleBooking);
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when cancelling an already cancelled booking")
    void cancelBooking_AlreadyCancelled_ThrowsException() {
        sampleBooking.setBookingStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(sampleBooking));

        assertThrows(InvalidBookingException.class, () -> bookingService.cancelBooking(1L));
        verify(packageRepository, never()).save(any(TravelPackage.class));
    }
}
