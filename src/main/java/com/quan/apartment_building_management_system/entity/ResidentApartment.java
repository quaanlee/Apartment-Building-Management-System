package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "ResidentApartment")
public class ResidentApartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ResidentApartmentID")
    private Integer residentApartmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProfileID", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApartmentID", nullable = false)
    private Apartment apartment;

    @Column(name = "MoveInDate", nullable = false)
    private LocalDate moveInDate;

    @Column(name = "MoveOutDate")
    private LocalDate moveOutDate;

    public ResidentApartment() {
    }

    public Integer getResidentApartmentId() {
        return residentApartmentId;
    }

    public void setResidentApartmentId(Integer residentApartmentId) {
        this.residentApartmentId = residentApartmentId;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public LocalDate getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(LocalDate moveOutDate) {
        this.moveOutDate = moveOutDate;
    }
}
