package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ApartmentImage")
public class ApartmentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageID")
    private Integer imageID;

    @Column(name = "ApartmentID", nullable = false)
    private Integer apartmentID;

    @Column(name = "ImageURL", nullable = false, length = 500, columnDefinition = "VARCHAR(500)")
    private String imageURL;

    @Column(name = "ImageTitle", length = 100)
    private String imageTitle;

    @Column(name = "IsPrimary", nullable = false)
    private Boolean isPrimary = false;

    @Column(name = "UploadedAt", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApartmentID", insertable = false, updatable = false)
    private Apartment apartment;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getImageID() { return imageID; }
    public void setImageID(Integer imageID) { this.imageID = imageID; }

    public Integer getApartmentID() { return apartmentID; }
    public void setApartmentID(Integer apartmentID) { this.apartmentID = apartmentID; }

    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public String getImageTitle() { return imageTitle; }
    public void setImageTitle(String imageTitle) { this.imageTitle = imageTitle; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public Apartment getApartment() { return apartment; }
    public void setApartment(Apartment apartment) { this.apartment = apartment; }
}
