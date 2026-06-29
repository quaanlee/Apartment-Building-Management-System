package com.quan.apartment_building_management_system.dto;

import com.quan.apartment_building_management_system.entity.Profile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDTO {
    // Account fields
    private Integer accountId;

    @NotBlank(message = "Username is required and can not only space")
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "Password is required and can not only space")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required and can not only space")
    private String roleName;

    private Boolean accountStatus = true;
    private LocalDateTime accountCreatedAt;
    private LocalDateTime lockedUntil;

    // Profile fields
    private Integer profileId;

    @NotBlank(message = "Full Name is required and can not only space")
    @Size(max = 100, message = "Full Name must not exceed 100 characters")
    private String fullName;

    private String gender;

    @Past(message = "Date of Birth must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(max = 100, message = "Place of Birth must not exceed 100 characters")
    private String placeOfBirth;

    @NotBlank(message = "Citizen ID is required and can not only space")
    @Pattern(regexp = "^[0-9]{12}$", message = "Citizen ID must be exactly 12 digits")
    private String citizenId;

    @Past(message = "Citizen ID Issue Date must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate citizenIdIssueDate;

    @Size(max = 100, message = "Citizen ID Issue Place must not exceed 100 characters")
    private String citizenIdIssuePlace;
    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;
    @Size(max = 50, message = "Ethnicity must not exceed 50 characters")
    private String ethnicity;

    @NotBlank(message = "Phone Number is required and can not only space")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone Number must contain exactly 10 digits and only number")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    private String avatarUrl;

    @Size(max = 100, message = "Emergency Contact Name must not exceed 100 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^$|^[0-9]{10}$", message = "Emergency Contact Phone must contain exactly 10 digits and only number")
    private String emergencyContactPhone;

    @Size(max = 50, message = "Relationship to Owner must not exceed 50 characters")
    private String relationshipToOwner;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate moveInDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate moveOutDate;

    private Boolean isHouseholdOwner = false;
    private Byte residentStatus = 1;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    // Default Constructor
    public UserDTO() {
    }

    // Mapping Constructor (Entity to DTO)
    public UserDTO(Profile profile) {
        if (profile != null) {
            this.profileId = profile.getProfileId();
            this.fullName = profile.getFullName();
            this.gender = profile.getGender();
            this.dateOfBirth = profile.getDateOfBirth();
            this.placeOfBirth = profile.getPlaceOfBirth();
            this.citizenId = profile.getCitizenId();
            this.citizenIdIssueDate = profile.getCitizenIdIssueDate();
            this.citizenIdIssuePlace = profile.getCitizenIdIssuePlace();
            this.nationality = profile.getNationality();
            this.ethnicity = profile.getEthnicity();
            this.phoneNumber = profile.getPhoneNumber();
            this.email = profile.getEmail();
            this.avatarUrl = profile.getAvatarUrl();
            this.emergencyContactName = profile.getEmergencyContactName();
            this.emergencyContactPhone = profile.getEmergencyContactPhone();
            this.relationshipToOwner = profile.getRelationshipToOwner();
            this.moveInDate = profile.getMoveInDate();
            this.moveOutDate = profile.getMoveOutDate();
            this.isHouseholdOwner = profile.getIsHouseholdOwner();
            this.residentStatus = profile.getResidentStatus();
            this.occupation = profile.getOccupation();

            if (profile.getAccount() != null) {
                this.accountId = profile.getAccount().getAccountId();
                this.username = profile.getAccount().getUsername();
                this.password = profile.getAccount().getPassword();
                this.roleName = profile.getAccount().getRole() != null ? profile.getAccount().getRole().getRoleName() : null;
                this.accountStatus = profile.getAccount().getStatus();
                this.accountCreatedAt = profile.getAccount().getCreatedAt();
                this.lockedUntil = profile.getAccount().getLockedUntil();
            }
        }
    }

    // Getters and Setters
    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Boolean getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(Boolean accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getAccountCreatedAt() {
        return accountCreatedAt;
    }

    public void setAccountCreatedAt(LocalDateTime accountCreatedAt) {
        this.accountCreatedAt = accountCreatedAt;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
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

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
