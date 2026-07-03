package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "MaintenanceReportImage")
public class MaintenanceReportImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageID")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReportID", nullable = false)
    private MaintenanceReport report;

    @Column(name = "ImageURL", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "Caption", length = 255)
    private String caption;

    public MaintenanceReportImage() {}

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public MaintenanceReport getReport() {
        return report;
    }

    public void setReport(MaintenanceReport report) {
        this.report = report;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
