package com.travel.management.service.impl;

import com.travel.management.dto.DestinationRequestDTO;
import com.travel.management.dto.DestinationResponseDTO;
import com.travel.management.dto.PageResponse;
import com.travel.management.entity.Destination;
import com.travel.management.exception.DuplicateResourceException;
import com.travel.management.exception.ResourceNotFoundException;
import com.travel.management.mapper.DestinationMapper;
import com.travel.management.repository.DestinationRepository;
import com.travel.management.service.DestinationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DestinationServiceImpl implements DestinationService {

    private final DestinationRepository destinationRepository;
    private final DestinationMapper destinationMapper;

    @Override
    public DestinationResponseDTO addDestination(DestinationRequestDTO requestDTO) {
        log.info("Adding new destination: {}", requestDTO.getName());
        String name = requestDTO.getName().trim();

        if (destinationRepository.existsByNameIgnoreCase(name)) {
            log.warn("Destination addition failed: Destination with name '{}' already exists", name);
            throw new DuplicateResourceException("Destination already exists with name: " + name);
        }

        Destination destination = destinationMapper.toEntity(requestDTO);
        Destination saved = destinationRepository.save(destination);
        log.info("Destination saved successfully with ID: {}", saved.getId());
        return destinationMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DestinationResponseDTO getDestinationById(Long id) {
        log.info("Fetching destination with ID: {}", id);
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", id));
        return destinationMapper.toDTO(destination);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DestinationResponseDTO> getAllDestinations(int page, int size, String sortBy, String sortDir) {
        log.info("Fetching destinations - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Destination> pageResult = destinationRepository.findAll(pageable);
        return PageResponse.from(pageResult.map(destinationMapper::toDTO));
    }

    @Override
    public DestinationResponseDTO updateDestination(Long id, DestinationRequestDTO requestDTO) {
        log.info("Updating destination ID: {}", id);
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", id));

        String newName = requestDTO.getName().trim();
        if (!destination.getName().equalsIgnoreCase(newName) && destinationRepository.existsByNameIgnoreCase(newName)) {
            log.warn("Update failed: Destination name '{}' is already taken", newName);
            throw new DuplicateResourceException("Destination name already in use: " + newName);
        }

        destinationMapper.updateEntityFromDTO(requestDTO, destination);
        Destination updated = destinationRepository.save(destination);
        log.info("Destination ID {} updated successfully", id);
        return destinationMapper.toDTO(updated);
    }

    @Override
    public void deleteDestination(Long id) {
        log.info("Deleting destination with ID: {}", id);
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", id));
        destinationRepository.delete(destination);
        log.info("Destination ID {} deleted successfully", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DestinationResponseDTO> searchDestinations(String query, int page, int size, String sortBy, String sortDir) {
        log.info("Searching destinations with query: '{}'", query);
        if (query == null || query.trim().isEmpty()) {
            return getAllDestinations(page, size, sortBy, sortDir);
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Destination> result = destinationRepository.searchDestinations(query.trim(), pageable);
        return PageResponse.from(result.map(destinationMapper::toDTO));
    }
}
