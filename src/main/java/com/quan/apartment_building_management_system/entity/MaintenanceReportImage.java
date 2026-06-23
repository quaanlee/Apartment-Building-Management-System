package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MaintenanceReportImage")
public class MaintenanceReportImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageID")
    private Long imageID;

    @Column(name = "ReportID", nullable = false)
    private Long reportID;

    @Column(name = "ImageURL", nullable = false, length = 500)
    private String imageURL;

    @Column(name = "Caption", length = 255)
    private String caption;

    // Mối quan hệ Nhiều - 1 về MaintenanceReport
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReportID", insertable = false, updatable = false)
    private MaintenanceReport maintenanceReport;

    // Getters and Setters
    public Long getImageID() { return imageID; }
    public void setImageID(Long imageID) { this.imageID = imageID; }

    public Long getReportID() { return reportID; }
    public void setReportID(Long reportID) { this.reportID = reportID; }

    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public MaintenanceReport getMaintenanceReport() { return maintenanceReport; }
    public void setMaintenanceReport(MaintenanceReport maintenanceReport) { this.maintenanceReport = maintenanceReport; }
}
