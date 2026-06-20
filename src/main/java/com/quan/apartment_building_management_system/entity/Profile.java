package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProfileID")
    private Integer profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountID")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApartmentID")
    private Apartment apartment;

    @Column(name = "FullName", nullable = false, length = 100)
    private String fullName;

    @Column(name = "Gender", length = 10)
    private String gender;

    @Column(name = "DateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "PlaceOfBirth", length = 100)
    private String placeOfBirth;

    @Column(name = "CitizenID", unique = true, length = 20)
    private String citizenId;

    @Column(name = "CitizenIDIssueDate")
    private LocalDate citizenIdIssueDate;

    @Column(name = "CitizenIDIssuePlace", length = 100)
    private String citizenIdIssuePlace;

    @Column(name = "Nationality", length = 50)
    private String nationality;

    @Column(name = "Ethnicity", length = 50)
    private String ethnicity;

    @Column(name = "Occupation", length = 100)
    private String occupation;

    @Column(name = "PhoneNumber", length = 15)
    private String phoneNumber;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "AvatarURL", length = 500)
    private String avatarUrl;

    @Column(name = "EmergencyContactName", length = 100)
    private String emergencyContactName;

    @Column(name = "EmergencyContactPhone", length = 15)
    private String emergencyContactPhone;

    @Column(name = "RelationshipToOwner", length = 50)
    private String relationshipToOwner;

    @Column(name = "MoveInDate")
    private LocalDate moveInDate;

    @Column(name = "MoveOutDate")
    private LocalDate moveOutDate;

    @Column(name = "IsHouseholdOwner", nullable = false)
    private Boolean isHouseholdOwner = true;

    @Column(name = "ResidentStatus", nullable = false)
    private Byte residentStatus = 1;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "profile")
    private List<Vehicle> vehicles = new ArrayList<>();

    @OneToMany(mappedBy = "profile")
    private List<ResidentApartment> residentApartments = new ArrayList<>();

    @OneToMany(mappedBy = "profile")
    private List<UtilityBooking> utilityBookings = new ArrayList<>();

    @OneToMany(mappedBy = "profile")
    private List<MaintenanceRequest> maintenanceRequests = new ArrayList<>();

    public Profile() {
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public LocalDate getCitizenIdIssueDate() {
        return citizenIdIssueDate;
    }

    public void setCitizenIdIssueDate(LocalDate citizenIdIssueDate) {
        this.citizenIdIssueDate = citizenIdIssueDate;
    }

    public String getCitizenIdIssuePlace() {
        return citizenIdIssuePlace;
    }

    public void setCitizenIdIssuePlace(String citizenIdIssuePlace) {
        this.citizenIdIssuePlace = citizenIdIssuePlace;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getRelationshipToOwner() {
        return relationshipToOwner;
    }

    public void setRelationshipToOwner(String relationshipToOwner) {
        this.relationshipToOwner = relationshipToOwner;
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

    public Boolean getIsHouseholdOwner() {
        return isHouseholdOwner;
    }

    public void setIsHouseholdOwner(Boolean householdOwner) {
        isHouseholdOwner = householdOwner;
    }

    public Byte getResidentStatus() {
        return residentStatus;
    }

    public void setResidentStatus(Byte residentStatus) {
        this.residentStatus = residentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public List<ResidentApartment> getResidentApartments() {
        return residentApartments;
    }

    public void setResidentApartments(List<ResidentApartment> residentApartments) {
        this.residentApartments = residentApartments;
    }

    public List<UtilityBooking> getUtilityBookings() {
        return utilityBookings;
    }

    public void setUtilityBookings(List<UtilityBooking> utilityBookings) {
        this.utilityBookings = utilityBookings;
    }

    public List<MaintenanceRequest> getMaintenanceRequests() {
        return maintenanceRequests;
    }

    public void setMaintenanceRequests(List<MaintenanceRequest> maintenanceRequests) {
        this.maintenanceRequests = maintenanceRequests;
    }
}
