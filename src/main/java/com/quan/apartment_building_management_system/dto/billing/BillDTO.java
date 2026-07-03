package com.quan.apartment_building_management_system.dto.billing;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BillDTO {

    @NotNull(message = "Apartment is required")
    private Integer apartmentId;

    @NotNull(message = "Billing Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Byte month;

    @NotNull(message = "Billing Year is required")
    @Min(value = 2000, message = "Year must be 2000 or greater")
    private Short year;

    private List<Integer> serviceIds = new ArrayList<>();
    private List<BigDecimal> quantities = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();

    public BillDTO() {
    }

    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
        this.apartmentId = apartmentId;
    }

    public Byte getMonth() {
        return month;
    }

    public void setMonth(Byte month) {
        this.month = month;
    }

    public Short getYear() {
        return year;
    }

    public void setYear(Short year) {
        this.year = year;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<BigDecimal> getQuantities() {
        return quantities;
    }

    public void setQuantities(List<BigDecimal> quantities) {
        this.quantities = quantities;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }
}
