package com.quan.apartment_building_management_system.dto.utility;

import com.quan.apartment_building_management_system.entity.Unit;
import com.quan.apartment_building_management_system.entity.Utility;
import com.quan.apartment_building_management_system.entity.UtilityImage;
import com.quan.apartment_building_management_system.entity.UtilityPrice;
import com.quan.apartment_building_management_system.entity.UtilityResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        String primaryImage = null;
        List<String> secondaryImages = new ArrayList<>();
        if (entity.getUtilityImages() != null) {
            for (UtilityImage image : entity.getUtilityImages()) {
                if (Boolean.TRUE.equals(image.getPrimary()) && primaryImage == null) {
                    primaryImage = image.getImageUrl();
                } else {
                    secondaryImages.add(image.getImageUrl());
                }
            }
        }
        List<UtilityDTO.Price> prices = entity.getUtilityPrices() != null
                ? entity.getUtilityPrices().stream().map(this::toUtilityPriceDTO).toList()
                : List.of();
        return new UtilityDTO.Resource(
                entity.getResourceId(),
                entity.getUtility() != null ? entity.getUtility().getUtilityId() : null,
                entity.getUtility() != null ? entity.getUtility().getUtilityName() : null,
                entity.getResourceName(),
                entity.getLocation(),
                entity.getDescription(),
                entity.getStatus(),
                primaryImage,
                secondaryImages,
                prices
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
                entity.getStatus(),
                entity.getType()
        );
        dto.setImageUrl(entity.getImageUrl());
        if (includeResources && entity.getUtilityResources() != null) {
            dto.setUtilityResources(entity.getUtilityResources().stream()
                    .map(this::toUtilityResourceDTO)
                    .toList());
        }
        if (entity.getUtilityResources() != null) {
            dto.setUtilityPrices(entity.getUtilityResources().stream()
                    .filter(res -> res.getUtilityPrices() != null)
                    .flatMap(res -> res.getUtilityPrices().stream())
                    .map(this::toUtilityPriceDTO)
                    .toList());
        }
        return dto;
    }

    public UtilityDTO.Price toUtilityPriceDTO(UtilityPrice entity) {
        if (entity == null) {
            return null;
        }
        UtilityDTO simpleUtility = null;
        UtilityDTO.Resource simpleResource = null;
        if (entity.getResource() != null) {
            UtilityResource r = entity.getResource();
            simpleResource = new UtilityDTO.Resource();
            simpleResource.setResourceId(r.getResourceId());
            simpleResource.setResourceName(r.getResourceName());
            simpleResource.setLocation(r.getLocation());
            simpleResource.setStatus(r.getStatus());
            if (r.getUtility() != null) {
                Utility u = r.getUtility();
                simpleResource.setUtilityId(u.getUtilityId());
                simpleResource.setUtilityName(u.getUtilityName());
                simpleUtility = new UtilityDTO(u.getUtilityId(), u.getUtilityName(), u.getDescription(), u.getStatus(), u.getType());
                simpleUtility.setImageUrl(u.getImageUrl());
            }
        }
        return new UtilityDTO.Price(
                entity.getUtilityPriceId(),
                simpleUtility,
                simpleResource,
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
        if (dto.getType() != null) {
            entity.setType(dto.getType());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        entity.setImageUrl(dto.getRawImageUrl());
    }

    public Utility toEntity(UtilityDTO dto) {
        if (dto == null) {
            return null;
        }
        Utility entity = new Utility();
        entity.setUtilityId(dto.getUtilityId());
        entity.setUtilityName(dto.getUtilityName());
        entity.setDescription(dto.getDescription());
        entity.setImageUrl(dto.getRawImageUrl());
        entity.setType(dto.getType() != null ? dto.getType() : true);
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
