package com.travel.management.service;

import com.travel.management.dto.DestinationRequestDTO;
import com.travel.management.dto.DestinationResponseDTO;
import com.travel.management.dto.PageResponse;

public interface DestinationService {

    DestinationResponseDTO addDestination(DestinationRequestDTO requestDTO);

    DestinationResponseDTO getDestinationById(Long id);

    PageResponse<DestinationResponseDTO> getAllDestinations(int page, int size, String sortBy, String sortDir);

    DestinationResponseDTO updateDestination(Long id, DestinationRequestDTO requestDTO);

    void deleteDestination(Long id);

    PageResponse<DestinationResponseDTO> searchDestinations(String query, int page, int size, String sortBy, String sortDir);
}
