package com.travel.management.controller;

import com.travel.management.dto.ApiResponse;
import com.travel.management.dto.DestinationRequestDTO;
import com.travel.management.dto.DestinationResponseDTO;
import com.travel.management.dto.PageResponse;
import com.travel.management.service.DestinationService;
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
@RequestMapping("/api/destinations")
@RequiredArgsConstructor
@Tag(name = "Destination Management", description = "Endpoints for managing travel destinations, cities, countries, and regional discovery")
public class DestinationController {

    private final DestinationService destinationService;

    @PostMapping
    @Operation(summary = "Add destination", description = "Creates a new travel destination with validation and uniqueness checks")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Destination added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Destination name already exists")
    })
    public ResponseEntity<ApiResponse<DestinationResponseDTO>> addDestination(
            @Valid @RequestBody DestinationRequestDTO requestDTO) {
        DestinationResponseDTO response = destinationService.addDestination(requestDTO);
        return new ResponseEntity<>(ApiResponse.success(response, "Destination added successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get destination by ID", description = "Retrieves destination information by primary identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Destination found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Destination not found")
    })
    public ResponseEntity<ApiResponse<DestinationResponseDTO>> getDestinationById(
            @Parameter(description = "Destination primary ID", example = "1") @PathVariable Long id) {
        DestinationResponseDTO response = destinationService.getDestinationById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Destination fetched successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all destinations", description = "Retrieves a paginated list of all destinations with sorting support")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Destinations fetched successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<DestinationResponseDTO>>> getAllDestinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<DestinationResponseDTO> response = destinationService.getAllDestinations(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Destinations fetched successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update destination", description = "Updates details of an existing destination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Destination updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Destination not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Destination name already in use")
    })
    public ResponseEntity<ApiResponse<DestinationResponseDTO>> updateDestination(
            @Parameter(description = "Destination primary ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody DestinationRequestDTO requestDTO) {
        DestinationResponseDTO response = destinationService.updateDestination(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Destination updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete destination", description = "Deletes a destination and its associated packages")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Destination deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Destination not found")
    })
    public ResponseEntity<Void> deleteDestination(
            @Parameter(description = "Destination primary ID", example = "1") @PathVariable Long id) {
        destinationService.deleteDestination(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search destinations", description = "Searches destinations by matching text across name, country, or state")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results returned successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<DestinationResponseDTO>>> searchDestinations(
            @Parameter(description = "Search query keyword", example = "Bali") @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<DestinationResponseDTO> response = destinationService.searchDestinations(query, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Search results fetched successfully"));
    }
}
