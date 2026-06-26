package com.quan.apartment_building_management_system.dto;

import com.quan.apartment_building_management_system.entity.Unit;
import com.quan.apartment_building_management_system.entity.Utility;
import com.quan.apartment_building_management_system.entity.UtilityPrice;
import com.quan.apartment_building_management_system.entity.UtilityResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminUtilityDTOHandler {

    // --- Entity to DTO conversion methods ---

    public UtilityDTO.Unit toUnitDTO(Unit entity) {
        if (entity == null) {
            return null;
        }
        return new UtilityDTO.Unit(entity.getUnitId(), entity.getUnitName());
    }

    public UtilityDTO.Resource toUtilityResourceDTO(UtilityResource entity) {
        if (entity == null) {
            return null;
        }
        return new UtilityDTO.Resource(
                entity.getResourceId(),
                entity.getUtility() != null ? entity.getUtility().getUtilityId() : null,
                entity.getUtility() != null ? entity.getUtility().getUtilityName() : null,
                entity.getResourceName(),
                entity.getLocation(),
                entity.getStatus()
        );
    }

    public UtilityDTO toUtilityDTO(Utility entity, boolean includeResources) {
        if (entity == null) {
            return null;
        }
        UtilityDTO dto = new UtilityDTO(
                entity.getUtilityId(),
                entity.getUtilityName(),
                entity.getDescription(),
                entity.getStatus()
        );
        if (includeResources && entity.getUtilityResources() != null) {
            dto.setUtilityResources(entity.getUtilityResources().stream()
                    .map(this::toUtilityResourceDTO)
                    .toList());
        }
        return dto;
    }

    public UtilityDTO.Price toUtilityPriceDTO(UtilityPrice entity) {
        if (entity == null) {
            return null;
        }
        return new UtilityDTO.Price(
                entity.getUtilityPriceId(),
                toUtilityDTO(entity.getUtility(), false),
                toUnitDTO(entity.getUnit()),
                entity.getPrice()
        );
    }

    public List<UtilityDTO.Unit> toUnitDTOList(List<Unit> entities) {
        return entities.stream().map(this::toUnitDTO).toList();
    }

    public List<UtilityDTO> toUtilityDTOList(List<Utility> entities, boolean includeResources) {
        return entities.stream().map(e -> toUtilityDTO(e, includeResources)).toList();
    }

    public List<UtilityDTO.Price> toUtilityPriceDTOList(List<UtilityPrice> entities) {
        return entities.stream().map(this::toUtilityPriceDTO).toList();
    }

    // --- DTO to Entity conversion / update methods ---

    public void updateEntityFromDTO(UtilityDTO dto, Utility entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setUtilityName(dto.getUtilityName());
        entity.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }

    public Utility toEntity(UtilityDTO dto) {
        if (dto == null) {
            return null;
        }
        Utility entity = new Utility();
        entity.setUtilityId(dto.getUtilityId());
        entity.setUtilityName(dto.getUtilityName());
        entity.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        } else {
            entity.setStatus(true); // default active
        }
        return entity;
    }

    // --- Helpers for Pagination & Stats ---

    public int calculateTotalPages(int totalItems, int size) {
        int totalPages = (int) Math.ceil((double) totalItems / size);
        return totalPages == 0 ? 1 : totalPages;
    }

    public int validatePage(int page, int totalPages) {
        if (page < 1) {
            return 1;
        }
        if (page > totalPages) {
            return totalPages;
        }
        return page;
    }

    public <T> List<T> getPaginatedList(List<T> list, int page, int size) {
        int totalItems = list.size();
        int totalPages = calculateTotalPages(totalItems, size);
        int validPage = validatePage(page, totalPages);
        int startIdx = (validPage - 1) * size;
        int endIdx = Math.min(startIdx + size, totalItems);
        return list.subList(startIdx, endIdx);
    }

    public long countActiveUtilities(List<Utility> utilities) {
        return utilities.stream().filter(Utility::getStatus).count();
    }
}
