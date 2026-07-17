package com.quan.apartment_building_management_system.dto.systemlog;

import java.math.BigDecimal;

public class UtilityPriceLogDTO {
    private Integer priceId;
    private String utilityName;
    private String resourceName;
    private String unitName;
    private BigDecimal price;

    public UtilityPriceLogDTO() {}

    public UtilityPriceLogDTO(Integer priceId, String utilityName, String resourceName, String unitName, BigDecimal price) {
        this.priceId = priceId;
        this.utilityName = utilityName;
        this.resourceName = resourceName;
        this.unitName = unitName;
        this.price = price;
    }

    public static UtilityPriceLogDTO fromEntity(com.quan.apartment_building_management_system.entity.UtilityPrice utilityPrice) {
        if (utilityPrice == null) return new UtilityPriceLogDTO();
        String utilityName = (utilityPrice.getResource() != null && utilityPrice.getResource().getUtility() != null)
                ? utilityPrice.getResource().getUtility().getUtilityName() : null;
        String resourceName = utilityPrice.getResource() != null ? utilityPrice.getResource().getResourceName() : null;
        String unitName = utilityPrice.getUnit() != null ? utilityPrice.getUnit().getUnitName() : null;
        return new UtilityPriceLogDTO(
                utilityPrice.getUtilityPriceId(),
                utilityName,
                resourceName,
                unitName,
                utilityPrice.getPrice()
        );
    }

    public static UtilityPriceLogDTO empty() {
        return new UtilityPriceLogDTO();
    }

    public Integer getPriceId() { return priceId; }
    public void setPriceId(Integer priceId) { this.priceId = priceId; }
    public String getUtilityName() { return utilityName; }
    public void setUtilityName(String utilityName) { this.utilityName = utilityName; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
