package com.quan.apartment_building_management_system.dto.systemlog;

public class ServiceLogDTO {
    private Integer serviceId;
    private String serviceName;
    private String serviceType;
    private java.math.BigDecimal unitPrice;
    private String unitName;
    private Boolean status;
    private String description;

    public ServiceLogDTO() {}

    public ServiceLogDTO(Integer serviceId, String serviceName, String serviceType, java.math.BigDecimal unitPrice, String unitName, Boolean status, String description) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.unitPrice = unitPrice;
        this.unitName = unitName;
        this.status = status;
        this.description = description;
    }

    public static ServiceLogDTO fromEntity(com.quan.apartment_building_management_system.entity.ServiceItem serviceItem) {
        if (serviceItem == null) return null;
        return new ServiceLogDTO(
            serviceItem.getServiceId(),
            serviceItem.getServiceName(),
            serviceItem.getServiceType(),
            serviceItem.getUnitPrice(),
            serviceItem.getUnit() != null ? serviceItem.getUnit().getUnitName() : null,
            serviceItem.getStatus(),
            serviceItem.getDescription()
        );
    }

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public java.math.BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
