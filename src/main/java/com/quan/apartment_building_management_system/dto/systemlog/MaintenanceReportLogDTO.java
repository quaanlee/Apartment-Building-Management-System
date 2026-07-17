package com.quan.apartment_building_management_system.dto.systemlog;

public class MaintenanceReportLogDTO {
    private Long reportId;
    private Byte progressPercent;
    private String reportContent;

    public MaintenanceReportLogDTO() {}

    public MaintenanceReportLogDTO(Long reportId, Byte progressPercent, String reportContent) {
        this.reportId = reportId;
        this.progressPercent = progressPercent;
        this.reportContent = reportContent;
    }

    public static MaintenanceReportLogDTO fromEntity(com.quan.apartment_building_management_system.entity.MaintenanceReport report) {
        if (report == null) return null;
        return new MaintenanceReportLogDTO(
            report.getReportId(),
            report.getProgressPercent(),
            report.getReportContent()
        );
    }

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public Byte getProgressPercent() { return progressPercent; }
    public void setProgressPercent(Byte progressPercent) { this.progressPercent = progressPercent; }
    public String getReportContent() { return reportContent; }
    public void setReportContent(String reportContent) { this.reportContent = reportContent; }
}
