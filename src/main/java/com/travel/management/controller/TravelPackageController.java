package com.travel.management.controller;

import com.travel.management.dto.ApiResponse;
import com.travel.management.dto.PageResponse;
import com.travel.management.dto.TravelPackageRequestDTO;
import com.travel.management.dto.TravelPackageResponseDTO;
import com.travel.management.enums.PackageStatus;
import com.travel.management.service.TravelPackageService;
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

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@Tag(name = "Travel Package Management", description = "Endpoints for creating, managing, filtering, and discovering travel packages")
public class TravelPackageController {

    private final TravelPackageService packageService;

    @PostMapping
    @Operation(summary = "Create package", description = "Creates a new travel package associated with an existing destination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Package created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or date inconsistency"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Destination not found")
    })
    public ResponseEntity<ApiResponse<TravelPackageResponseDTO>> createPackage(
            @Valid @RequestBody TravelPackageRequestDTO requestDTO) {
        TravelPackageResponseDTO response = packageService.createPackage(requestDTO);
        return new ResponseEntity<>(ApiResponse.success(response, "Package created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get package by ID", description = "Retrieves complete itinerary and details of a travel package")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Package found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<ApiResponse<TravelPackageResponseDTO>> getPackageById(
            @Parameter(description = "Travel Package ID", example = "1") @PathVariable Long id) {
        TravelPackageResponseDTO response = packageService.getPackageById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Package fetched successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all packages", description = "Retrieves a paginated list of all travel packages")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Packages fetched successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<TravelPackageResponseDTO>>> getAllPackages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<TravelPackageResponseDTO> response = packageService.getAllPackages(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Packages fetched successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update package", description = "Updates details, price, dates, or seat availability of an existing package")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Package updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Package or destination not found")
    })
    public ResponseEntity<ApiResponse<TravelPackageResponseDTO>> updatePackage(
            @Parameter(description = "Travel Package ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody TravelPackageRequestDTO requestDTO) {
        TravelPackageResponseDTO response = packageService.updatePackage(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Package updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete package", description = "Deletes a travel package and associated bookings")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Package deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<Void> deletePackage(
            @Parameter(description = "Travel Package ID", example = "1") @PathVariable Long id) {
        packageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @Operation(summary = "Search/filter packages", description = "Filters packages by destination, price range, duration, status, or keyword")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Filtered packages fetched successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<TravelPackageResponseDTO>>> filterPackages(
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) PackageStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<TravelPackageResponseDTO> response = packageService.filterPackages(
                destinationId, minPrice, maxPrice, minDuration, maxDuration, status, keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Filtered packages fetched successfully"));
    }

    @GetMapping("/destination/{destinationId}")
    @Operation(summary = "Get packages by destination", description = "Retrieves all packages configured for a specific destination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Packages fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Destination not found")
    })
    public ResponseEntity<ApiResponse<PageResponse<TravelPackageResponseDTO>>> getPackagesByDestination(
            @Parameter(description = "Destination ID", example = "1") @PathVariable Long destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<TravelPackageResponseDTO> response = packageService.getPackagesByDestination(
                destinationId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Packages for destination fetched successfully"));
    }

    @GetMapping("/available")
    @Operation(summary = "Find available packages", description = "Retrieves packages that have ACTIVE status and seats available (> 0)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Available packages fetched successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<TravelPackageResponseDTO>>> getAvailablePackages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<TravelPackageResponseDTO> response = packageService.getAvailablePackages(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Available packages fetched successfully"));
    }
}
