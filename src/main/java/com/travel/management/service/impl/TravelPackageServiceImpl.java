package com.travel.management.service.impl;

import com.travel.management.dto.PageResponse;
import com.travel.management.dto.TravelPackageRequestDTO;
import com.travel.management.dto.TravelPackageResponseDTO;
import com.travel.management.entity.Destination;
import com.travel.management.entity.TravelPackage;
import com.travel.management.enums.PackageStatus;
import com.travel.management.exception.BadRequestException;
import com.travel.management.exception.ResourceNotFoundException;
import com.travel.management.mapper.TravelPackageMapper;
import com.travel.management.repository.DestinationRepository;
import com.travel.management.repository.TravelPackageRepository;
import com.travel.management.service.TravelPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TravelPackageServiceImpl implements TravelPackageService {

    private final TravelPackageRepository packageRepository;
    private final DestinationRepository destinationRepository;
    private final TravelPackageMapper packageMapper;

    @Override
    public TravelPackageResponseDTO createPackage(TravelPackageRequestDTO requestDTO) {
        log.info("Creating new travel package: {}", requestDTO.getPackageName());

        if (requestDTO.getEndDate().isBefore(requestDTO.getStartDate())) {
            throw new BadRequestException("Package end date cannot be before start date");
        }

        Destination destination = destinationRepository.findById(requestDTO.getDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", requestDTO.getDestinationId()));

        TravelPackage travelPackage = packageMapper.toEntity(requestDTO, destination);

        // Auto-adjust status based on seats
        if (travelPackage.getAvailableSeats() == 0) {
            travelPackage.setStatus(PackageStatus.SOLD_OUT);
        }

        TravelPackage saved = packageRepository.save(travelPackage);
        log.info("Travel package created successfully with ID: {}", saved.getId());
        return packageMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TravelPackageResponseDTO getPackageById(Long id) {
        log.info("Fetching travel package with ID: {}", id);
        TravelPackage travelPackage = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TravelPackage", "id", id));
        return packageMapper.toDTO(travelPackage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TravelPackageResponseDTO> getAllPackages(int page, int size, String sortBy, String sortDir) {
        log.info("Fetching all packages - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TravelPackage> pageResult = packageRepository.findAll(pageable);
        return PageResponse.from(pageResult.map(packageMapper::toDTO));
    }

    @Override
    public TravelPackageResponseDTO updatePackage(Long id, TravelPackageRequestDTO requestDTO) {
        log.info("Updating travel package with ID: {}", id);

        if (requestDTO.getEndDate().isBefore(requestDTO.getStartDate())) {
            throw new BadRequestException("Package end date cannot be before start date");
        }

        TravelPackage existingPackage = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TravelPackage", "id", id));

        Destination destination = existingPackage.getDestination();
        if (!existingPackage.getDestination().getId().equals(requestDTO.getDestinationId())) {
            destination = destinationRepository.findById(requestDTO.getDestinationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", requestDTO.getDestinationId()));
        }

        packageMapper.updateEntityFromDTO(requestDTO, existingPackage, destination);

        // Adjust status if seats reached 0 or became available
        if (existingPackage.getAvailableSeats() == 0 && existingPackage.getStatus() == PackageStatus.ACTIVE) {
            existingPackage.setStatus(PackageStatus.SOLD_OUT);
        } else if (existingPackage.getAvailableSeats() > 0 && existingPackage.getStatus() == PackageStatus.SOLD_OUT) {
            existingPackage.setStatus(PackageStatus.ACTIVE);
        }

        TravelPackage updated = packageRepository.save(existingPackage);
        log.info("Travel package ID {} updated successfully", id);
        return packageMapper.toDTO(updated);
    }

    @Override
    public void deletePackage(Long id) {
        log.info("Deleting travel package with ID: {}", id);
        TravelPackage travelPackage = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TravelPackage", "id", id));
        packageRepository.delete(travelPackage);
        log.info("Travel package ID {} deleted successfully", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TravelPackageResponseDTO> filterPackages(
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
            String sortDir) {

        log.info("Filtering travel packages with destinationId={}, minPrice={}, maxPrice={}, status={}, keyword={}",
                destinationId, minPrice, maxPrice, status, keyword);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TravelPackage> results = packageRepository.filterPackages(
                destinationId, minPrice, maxPrice, minDuration, maxDuration, status, keyword, pageable);

        return PageResponse.from(results.map(packageMapper::toDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TravelPackageResponseDTO> getPackagesByDestination(
            Long destinationId, int page, int size, String sortBy, String sortDir) {

        if (!destinationRepository.existsById(destinationId)) {
            throw new ResourceNotFoundException("Destination", "id", destinationId);
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TravelPackage> results = packageRepository.findByDestinationId(destinationId, pageable);
        return PageResponse.from(results.map(packageMapper::toDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TravelPackageResponseDTO> getAvailablePackages(int page, int size, String sortBy, String sortDir) {
        log.info("Fetching currently available active packages with seats > 0");
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TravelPackage> results = packageRepository.findByStatusAndAvailableSeatsGreaterThan(
                PackageStatus.ACTIVE, 0, pageable);
        return PageResponse.from(results.map(packageMapper::toDTO));
    }
}
