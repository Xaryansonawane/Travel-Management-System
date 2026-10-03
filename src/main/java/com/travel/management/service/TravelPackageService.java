package com.travel.management.service;

import com.travel.management.dto.PageResponse;
import com.travel.management.dto.TravelPackageRequestDTO;
import com.travel.management.dto.TravelPackageResponseDTO;
import com.travel.management.enums.PackageStatus;

import java.math.BigDecimal;

public interface TravelPackageService {

    TravelPackageResponseDTO createPackage(TravelPackageRequestDTO requestDTO);

    TravelPackageResponseDTO getPackageById(Long id);

    PageResponse<TravelPackageResponseDTO> getAllPackages(int page, int size, String sortBy, String sortDir);

    TravelPackageResponseDTO updatePackage(Long id, TravelPackageRequestDTO requestDTO);

    void deletePackage(Long id);

    PageResponse<TravelPackageResponseDTO> filterPackages(
            Long destinationId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minDuration,
            Integer maxDuration,
            PackageStatus status,
            String keyword,
            int page,
            int size,
            String sortBy,
            String sortDir);

    PageResponse<TravelPackageResponseDTO> getPackagesByDestination(
            Long destinationId,
            int page,
            int size,
            String sortBy,
            String sortDir);

    PageResponse<TravelPackageResponseDTO> getAvailablePackages(
            int page,
            int size,
            String sortBy,
            String sortDir);
}
