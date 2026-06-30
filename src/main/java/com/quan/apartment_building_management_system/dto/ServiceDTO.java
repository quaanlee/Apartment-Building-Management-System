package com.quan.apartment_building_management_system.dto;

import com.quan.apartment_building_management_system.entity.ServiceItem;
import java.math.BigDecimal;

public class ServiceDTO {
    private Integer serviceId;
    private String serviceName;
    private String serviceType;
    private BigDecimal unitPrice;
    private Integer unitId;
    private String unitName;
    private Boolean status;
    private String description;

    public ServiceDTO() {
    }

    public ServiceDTO(ServiceItem item) {
        if (item != null) {
            this.serviceId = item.getServiceId();
            this.serviceName = item.getServiceName();
            this.serviceType = item.getServiceType();
            this.unitPrice = item.getUnitPrice();
            this.status = item.getStatus();
            this.description = item.getDescription();
            if (item.getUnit() != null) {
                this.unitId = item.getUnit().getUnitId();
                this.unitName = item.getUnit().getUnitName();
            }
        }
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
