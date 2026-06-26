package com.quan.apartment_building_management_system.dto.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UtilityDTO {
    private Integer utilityId;
    private String utilityName;
    private String description;
    private Boolean status;
    private List<Resource> utilityResources = new ArrayList<>();
    private List<Price> utilityPrices = new ArrayList<>();

    public UtilityDTO() {
    }

    public UtilityDTO(Integer utilityId, String utilityName, String description, Boolean status) {
        this.utilityId = utilityId;
        this.utilityName = utilityName;
        this.description = description;
        this.status = status;
    }

    public Integer getUtilityId() {
        return utilityId;
    }

    public void setUtilityId(Integer utilityId) {
        this.utilityId = utilityId;
    }

    public String getUtilityName() {
        return utilityName;
    }

    public void setUtilityName(String utilityName) {
        this.utilityName = utilityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Resource> getUtilityResources() {
        return utilityResources;
    }

    public void setUtilityResources(List<Resource> utilityResources) {
        this.utilityResources = utilityResources;
    }

    public List<Price> getUtilityPrices() {
        return utilityPrices;
    }

    public void setUtilityPrices(List<Price> utilityPrices) {
        this.utilityPrices = utilityPrices;
    }

    // --- Static Nested DTOs ---

    public static class Resource {
        private Integer resourceId;
        private Integer utilityId;
        private String utilityName;
        private String resourceName;
        private String location;
        private Boolean status;

        public Resource() {
        }

        public Resource(Integer resourceId, Integer utilityId, String utilityName, String resourceName, String location, Boolean status) {
            this.resourceId = resourceId;
            this.utilityId = utilityId;
            this.utilityName = utilityName;
            this.resourceName = resourceName;
            this.location = location;
            this.status = status;
        }

        public Integer getResourceId() {
            return resourceId;
        }

        public void setResourceId(Integer resourceId) {
            this.resourceId = resourceId;
        }

        public Integer getUtilityId() {
            return utilityId;
        }

        public void setUtilityId(Integer utilityId) {
            this.utilityId = utilityId;
        }

        public String getUtilityName() {
            return utilityName;
        }

        public void setUtilityName(String utilityName) {
            this.utilityName = utilityName;
        }

        public String getResourceName() {
            return resourceName;
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }
    }

    public static class Price {
        private Integer utilityPriceId;
        private UtilityDTO utility;
        private Unit unit;
        private BigDecimal price;

        public Price() {
        }

        public Price(Integer utilityPriceId, UtilityDTO utility, Unit unit, BigDecimal price) {
            this.utilityPriceId = utilityPriceId;
            this.utility = utility;
            this.unit = unit;
            this.price = price;
        }

        public Integer getUtilityPriceId() {
            return utilityPriceId;
        }

        public void setUtilityPriceId(Integer utilityPriceId) {
            this.utilityPriceId = utilityPriceId;
        }

        public UtilityDTO getUtility() {
            return utility;
        }

        public void setUtility(UtilityDTO utility) {
            this.utility = utility;
        }

        public Unit getUnit() {
            return unit;
        }

        public void setUnit(Unit unit) {
            this.unit = unit;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    public static class Unit {
        private Integer unitId;
        private String unitName;

        public Unit() {
        }

        public Unit(Integer unitId, String unitName) {
            this.unitId = unitId;
            this.unitName = unitName;
        }

        public Integer getUnitId() {
            return unitId;
        }

        public void setUnitId(Integer unitId) {
            this.unitId = unitId;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }
    }
}
