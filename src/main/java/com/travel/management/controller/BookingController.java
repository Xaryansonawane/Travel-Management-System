package com.travel.management.controller;

import com.travel.management.dto.ApiResponse;
import com.travel.management.dto.BookingRequestDTO;
import com.travel.management.dto.BookingResponseDTO;
import com.travel.management.dto.BookingStatusUpdateDTO;
import com.travel.management.dto.PageResponse;
import com.travel.management.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking Management", description = "Endpoints for creating, managing, tracking, updating, and cancelling travel bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create booking", description = "Books traveler seats for a package, calculates total cost, and reduces seat inventory atomically")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Insufficient seats or invalid package status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User or package not found")
    })
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @Valid @RequestBody BookingRequestDTO requestDTO) {
        BookingResponseDTO response = bookingService.createBooking(requestDTO);
        return new ResponseEntity<>(ApiResponse.success(response, "Booking created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieves reservation details, payment status, and traveler counts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingById(
            @Parameter(description = "Booking ID", example = "1") @PathVariable Long id) {
        BookingResponseDTO response = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Booking fetched successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all bookings", description = "Retrieves a paginated list of all bookings in the system")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bookings fetched successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponse<BookingResponseDTO> response = bookingService.getAllBookings(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Bookings fetched successfully"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get bookings by user", description = "Retrieves all bookings made by a specific user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User bookings fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getBookingsByUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponse<BookingResponseDTO> response = bookingService.getBookingsByUser(userId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "User bookings fetched successfully"));
    }

    @GetMapping("/package/{packageId}")
    @Operation(summary = "Get bookings by package", description = "Retrieves all bookings associated with a specific travel package")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Package bookings fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getBookingsByPackage(
            @Parameter(description = "Travel Package ID", example = "1") @PathVariable Long packageId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponse<BookingResponseDTO> response = bookingService.getBookingsByPackage(packageId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Package bookings fetched successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking", description = "Updates booking status, payment status, or number of travelers")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status update or insufficient seats"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBooking(
            @Parameter(description = "Booking ID", example = "1") @PathVariable Long id,
            @RequestBody BookingStatusUpdateDTO updateDTO) {
        BookingResponseDTO response = bookingService.updateBooking(id, updateDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Booking updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel booking", description = "Cancels a booking, releases reserved seats back to the package, and initiates refund if paid")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Booking is already cancelled"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBooking(
            @Parameter(description = "Booking ID", example = "1") @PathVariable Long id) {
        BookingResponseDTO response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Booking cancelled successfully and seats restored"));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking (PUT alias)", description = "Alternative endpoint to cancel a booking and restore package seats")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBookingViaPut(
            @Parameter(description = "Booking ID", example = "1") @PathVariable Long id) {
        BookingResponseDTO response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Booking cancelled successfully and seats restored"));
    }
}
