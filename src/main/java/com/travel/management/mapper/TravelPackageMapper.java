package com.travel.management.mapper;

import com.travel.management.dto.TravelPackageRequestDTO;
import com.travel.management.dto.TravelPackageResponseDTO;
import com.travel.management.entity.Destination;
import com.travel.management.entity.TravelPackage;
import com.travel.management.enums.PackageStatus;
import org.springframework.stereotype.Component;

@Component
public class TravelPackageMapper {

    public TravelPackage toEntity(TravelPackageRequestDTO dto, Destination destination) {
        if (dto == null) {
            return null;
        }
        return TravelPackage.builder()
                .packageName(dto.getPackageName().trim())
                .destination(destination)
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .duration(dto.getDuration())
                .price(dto.getPrice())
                .availableSeats(dto.getAvailableSeats())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .inclusions(dto.getInclusions() != null ? dto.getInclusions().trim() : null)
                .exclusions(dto.getExclusions() != null ? dto.getExclusions().trim() : null)
                .status(dto.getStatus() != null ? dto.getStatus() : PackageStatus.ACTIVE)
                .build();
    }

    public TravelPackageResponseDTO toDTO(TravelPackage entity) {
        if (entity == null) {
            return null;
        }
        Long destinationId = entity.getDestination() != null ? entity.getDestination().getId() : null;
        String destinationName = entity.getDestination() != null ? entity.getDestination().getName() : null;
        String destinationCountry = entity.getDestination() != null ? entity.getDestination().getCountry() : null;

        return TravelPackageResponseDTO.builder()
                .id(entity.getId())
                .packageName(entity.getPackageName())
                .destinationId(destinationId)
                .destinationName(destinationName)
                .destinationCountry(destinationCountry)
                .description(entity.getDescription())
                .duration(entity.getDuration())
                .price(entity.getPrice())
                .availableSeats(entity.getAvailableSeats())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .inclusions(entity.getInclusions())
                .exclusions(entity.getExclusions())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(TravelPackageRequestDTO dto, TravelPackage entity, Destination destination) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setPackageName(dto.getPackageName().trim());
        if (destination != null) {
            entity.setDestination(destination);
        }
        entity.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        entity.setDuration(dto.getDuration());
        entity.setPrice(dto.getPrice());
        entity.setAvailableSeats(dto.getAvailableSeats());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setInclusions(dto.getInclusions() != null ? dto.getInclusions().trim() : null);
        entity.setExclusions(dto.getExclusions() != null ? dto.getExclusions().trim() : null);
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }
}
