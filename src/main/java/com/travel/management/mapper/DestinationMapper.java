package com.travel.management.mapper;

import com.travel.management.dto.DestinationRequestDTO;
import com.travel.management.dto.DestinationResponseDTO;
import com.travel.management.entity.Destination;
import org.springframework.stereotype.Component;

@Component
public class DestinationMapper {

    public Destination toEntity(DestinationRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Destination.builder()
                .name(dto.getName().trim())
                .country(dto.getCountry().trim())
                .state(dto.getState().trim())
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .bestTimeToVisit(dto.getBestTimeToVisit() != null ? dto.getBestTimeToVisit().trim() : null)
                .imageUrl(dto.getImageUrl() != null ? dto.getImageUrl().trim() : null)
                .build();
    }

    public DestinationResponseDTO toDTO(Destination entity) {
        if (entity == null) {
            return null;
        }
        return DestinationResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .country(entity.getCountry())
                .state(entity.getState())
                .description(entity.getDescription())
                .bestTimeToVisit(entity.getBestTimeToVisit())
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntityFromDTO(DestinationRequestDTO dto, Destination entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setName(dto.getName().trim());
        entity.setCountry(dto.getCountry().trim());
        entity.setState(dto.getState().trim());
        entity.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        entity.setBestTimeToVisit(dto.getBestTimeToVisit() != null ? dto.getBestTimeToVisit().trim() : null);
        entity.setImageUrl(dto.getImageUrl() != null ? dto.getImageUrl().trim() : null);
    }
}
