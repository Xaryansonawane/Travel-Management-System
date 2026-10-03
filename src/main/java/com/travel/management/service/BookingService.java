package com.travel.management.service;

import com.travel.management.dto.BookingRequestDTO;
import com.travel.management.dto.BookingResponseDTO;
import com.travel.management.dto.BookingStatusUpdateDTO;
import com.travel.management.dto.PageResponse;

public interface BookingService {

    BookingResponseDTO createBooking(BookingRequestDTO requestDTO);

    BookingResponseDTO getBookingById(Long id);

    PageResponse<BookingResponseDTO> getAllBookings(int page, int size, String sortBy, String sortDir);

    PageResponse<BookingResponseDTO> getBookingsByUser(Long userId, int page, int size, String sortBy, String sortDir);

    PageResponse<BookingResponseDTO> getBookingsByPackage(Long packageId, int page, int size, String sortBy, String sortDir);

    BookingResponseDTO updateBooking(Long id, BookingStatusUpdateDTO updateDTO);

    BookingResponseDTO cancelBooking(Long id);
}
