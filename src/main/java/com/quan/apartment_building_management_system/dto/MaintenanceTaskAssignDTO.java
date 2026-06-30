package com.quan.apartment_building_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MaintenanceTaskAssignDTO {

    @NotNull(message = "Request ID is required")
    private Integer requestId;

    @NotNull(message = "Staff ID is required")
    private Integer staffId;

    @NotBlank(message = "Deadline is required")
    private String deadline;

    public MaintenanceTaskAssignDTO() {
    }

    public MaintenanceTaskAssignDTO(Integer requestId, Integer staffId, String deadline) {
        this.requestId = requestId;
        this.staffId = staffId;
        this.deadline = deadline;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
