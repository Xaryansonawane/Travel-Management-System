package com.travel.management.service;

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
import com.travel.management.service.impl.TravelPackageServiceImpl;
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

import java.math.BigDecimal;
import java.time.LocalDate;
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
class TravelPackageServiceImplTest {

    @Mock
    private TravelPackageRepository packageRepository;

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private TravelPackageMapper packageMapper;

    @InjectMocks
    private TravelPackageServiceImpl packageService;

    private Destination sampleDestination;
    private TravelPackage samplePackage;
    private TravelPackageRequestDTO requestDTO;
    private TravelPackageResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        sampleDestination = Destination.builder()
                .id(1L)
                .name("Swiss Alps")
                .country("Switzerland")
                .state("Valais")
                .build();

        samplePackage = TravelPackage.builder()
                .id(1L)
                .packageName("Swiss Alps Expedition")
                .destination(sampleDestination)
                .duration(6)
                .price(new BigDecimal("1850.00"))
                .availableSeats(10)
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(1).plusDays(6))
                .status(PackageStatus.ACTIVE)
                .build();

        requestDTO = TravelPackageRequestDTO.builder()
                .packageName("Swiss Alps Expedition")
                .destinationId(1L)
                .duration(6)
                .price(new BigDecimal("1850.00"))
                .availableSeats(10)
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(1).plusDays(6))
                .status(PackageStatus.ACTIVE)
                .build();

        responseDTO = TravelPackageResponseDTO.builder()
                .id(1L)
                .packageName("Swiss Alps Expedition")
                .destinationId(1L)
                .destinationName("Swiss Alps")
                .destinationCountry("Switzerland")
                .duration(6)
                .price(new BigDecimal("1850.00"))
                .availableSeats(10)
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(1).plusDays(6))
                .status(PackageStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a travel package")
    void createPackage_Success() {
        when(destinationRepository.findById(1L)).thenReturn(Optional.of(sampleDestination));
        when(packageMapper.toEntity(requestDTO, sampleDestination)).thenReturn(samplePackage);
        when(packageRepository.save(samplePackage)).thenReturn(samplePackage);
        when(packageMapper.toDTO(samplePackage)).thenReturn(responseDTO);

        TravelPackageResponseDTO result = packageService.createPackage(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getPackageName()).isEqualTo("Swiss Alps Expedition");
        verify(packageRepository).save(samplePackage);
    }

    @Test
    @DisplayName("Should throw BadRequestException when end date is before start date")
    void createPackage_EndDateBeforeStartDate_ThrowsException() {
        requestDTO.setStartDate(LocalDate.now().plusDays(10));
        requestDTO.setEndDate(LocalDate.now().plusDays(5));

        assertThrows(BadRequestException.class, () -> packageService.createPackage(requestDTO));
        verify(packageRepository, never()).save(any(TravelPackage.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when destination does not exist")
    void createPackage_DestinationNotFound_ThrowsException() {
        when(destinationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> packageService.createPackage(requestDTO));
        verify(packageRepository, never()).save(any(TravelPackage.class));
    }

    @Test
    @DisplayName("Should filter packages based on search criteria")
    void filterPackages_Success() {
        Page<TravelPackage> page = new PageImpl<>(List.of(samplePackage));
        when(packageRepository.filterPackages(
                eq(1L), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);
        when(packageMapper.toDTO(samplePackage)).thenReturn(responseDTO);

        PageResponse<TravelPackageResponseDTO> result = packageService.filterPackages(
                1L, null, null, null, null, null, null, 0, 10, "id", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should get available packages with status ACTIVE and seats > 0")
    void getAvailablePackages_Success() {
        Page<TravelPackage> page = new PageImpl<>(List.of(samplePackage));
        when(packageRepository.findByStatusAndAvailableSeatsGreaterThan(eq(PackageStatus.ACTIVE), eq(0), any(Pageable.class)))
                .thenReturn(page);
        when(packageMapper.toDTO(samplePackage)).thenReturn(responseDTO);

        PageResponse<TravelPackageResponseDTO> result = packageService.getAvailablePackages(0, 10, "id", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }
}
