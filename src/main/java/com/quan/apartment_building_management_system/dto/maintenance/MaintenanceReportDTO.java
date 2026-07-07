package com.quan.apartment_building_management_system.dto.maintenance;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MaintenanceReportDTO {

    @NotNull(message = "Task ID is required")
    private Integer taskId;

    @NotBlank(message = "Report content is required")
    private String reportContent;

    @NotNull(message = "Progress percent is required")
    @Min(value = 0, message = "Progress cannot be less than 0")
    @Max(value = 100, message = "Progress cannot be more than 100")
    private Byte progressPercent;

    public MaintenanceReportDTO() {
    }

    public MaintenanceReportDTO(Integer taskId, String reportContent, Byte progressPercent) {
        this.taskId = taskId;
        this.reportContent = reportContent;
        this.progressPercent = progressPercent;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public Byte getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Byte progressPercent) {
        this.progressPercent = progressPercent;
    }
}
