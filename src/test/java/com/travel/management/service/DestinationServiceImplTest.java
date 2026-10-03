package com.travel.management.service;

import com.travel.management.dto.DestinationRequestDTO;
import com.travel.management.dto.DestinationResponseDTO;
import com.travel.management.dto.PageResponse;
import com.travel.management.entity.Destination;
import com.travel.management.exception.DuplicateResourceException;
import com.travel.management.exception.ResourceNotFoundException;
import com.travel.management.mapper.DestinationMapper;
import com.travel.management.repository.DestinationRepository;
import com.travel.management.service.impl.DestinationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DestinationServiceImplTest {

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private DestinationMapper destinationMapper;

    @InjectMocks
    private DestinationServiceImpl destinationService;

    private Destination sampleDestination;
    private DestinationRequestDTO requestDTO;
    private DestinationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        sampleDestination = Destination.builder()
                .id(1L)
                .name("Paris")
                .country("France")
                .state("Île-de-France")
                .description("City of Light")
                .build();

        requestDTO = DestinationRequestDTO.builder()
                .name("Paris")
                .country("France")
                .state("Île-de-France")
                .description("City of Light")
                .build();

        responseDTO = DestinationResponseDTO.builder()
                .id(1L)
                .name("Paris")
                .country("France")
                .state("Île-de-France")
                .description("City of Light")
                .build();
    }

    @Test
    @DisplayName("Should successfully add a new destination")
    void addDestination_Success() {
        when(destinationRepository.existsByNameIgnoreCase("Paris")).thenReturn(false);
        when(destinationMapper.toEntity(requestDTO)).thenReturn(sampleDestination);
        when(destinationRepository.save(sampleDestination)).thenReturn(sampleDestination);
        when(destinationMapper.toDTO(sampleDestination)).thenReturn(responseDTO);

        DestinationResponseDTO result = destinationService.addDestination(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Paris");
        verify(destinationRepository).save(sampleDestination);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when destination name already exists")
    void addDestination_DuplicateName_ThrowsException() {
        when(destinationRepository.existsByNameIgnoreCase("Paris")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> destinationService.addDestination(requestDTO));
        verify(destinationRepository, never()).save(any(Destination.class));
    }

    @Test
    @DisplayName("Should retrieve destination when found by ID")
    void getDestinationById_Success() {
        when(destinationRepository.findById(1L)).thenReturn(Optional.of(sampleDestination));
        when(destinationMapper.toDTO(sampleDestination)).thenReturn(responseDTO);

        DestinationResponseDTO result = destinationService.getDestinationById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when destination ID does not exist")
    void getDestinationById_NotFound_ThrowsException() {
        when(destinationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> destinationService.getDestinationById(99L));
    }

    @Test
    @DisplayName("Should search destinations matching keyword query")
    void searchDestinations_Success() {
        Page<Destination> page = new PageImpl<>(List.of(sampleDestination));
        when(destinationRepository.searchDestinations(eq("Paris"), any(Pageable.class))).thenReturn(page);
        when(destinationMapper.toDTO(sampleDestination)).thenReturn(responseDTO);

        PageResponse<DestinationResponseDTO> result = destinationService.searchDestinations("Paris", 0, 10, "name", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }
}
