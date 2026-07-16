package com.quan.apartment_building_management_system.dto.user;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.EmployeeProfile;
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
    private Integer accountId;

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
    private Integer employeeProfileId;

    @NotBlank(message = "Full Name is required and can not only space")
    @Size(max = 100, message = "Full Name must not exceed 100 characters")
    private String fullName;

    private String gender;

    @Past(message = "Date of Birth must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(max = 100, message = "Place of Birth must not exceed 100 characters")
    private String placeOfBirth;

    @Pattern(regexp = "^$|^[0-9]{12}$", message = "Citizen ID must be exactly 12 digits")
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

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

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

    private Integer apartmentId;
    private String apartmentNumber;

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

            if (profile.getApartment() != null) {
                this.apartmentId = profile.getApartment().getApartmentId();
                this.apartmentNumber = profile.getApartment().getApartmentNumber();
            }

            if (profile.getAccount() != null) {
                this.accountId = profile.getAccount().getAccountId();
                this.password = profile.getAccount().getPassword();
                this.roleName = profile.getAccount().getRole() != null ? profile.getAccount().getRole().getRoleName()
                        : null;
                this.accountStatus = profile.getAccount().getStatus();
                this.accountCreatedAt = profile.getAccount().getCreatedAt();
                this.lockedUntil = profile.getAccount().getLockedUntil();
            }
        }
    }

    public UserDTO(EmployeeProfile employeeProfile) {
        if (employeeProfile != null) {
            this.employeeProfileId = employeeProfile.getEmployeeProfileId();
            this.fullName = employeeProfile.getFullName();
            this.gender = employeeProfile.getGender() != null ? (employeeProfile.getGender() ? "Nam" : "Nữ") : null;
            this.dateOfBirth = employeeProfile.getDateOfBirth();
            this.phoneNumber = employeeProfile.getPhoneNumber();
            this.email = employeeProfile.getEmail();
            this.avatarUrl = employeeProfile.getAvatarUrl();
            this.address = employeeProfile.getAddress();

            if (employeeProfile.getAccount() != null) {
                this.accountId = employeeProfile.getAccount().getAccountId();
                this.password = employeeProfile.getAccount().getPassword();
                this.roleName = employeeProfile.getAccount().getRole() != null ? employeeProfile.getAccount().getRole().getRoleName() : null;
                this.accountStatus = employeeProfile.getAccount().getStatus();
                this.accountCreatedAt = employeeProfile.getAccount().getCreatedAt();
                this.lockedUntil = employeeProfile.getAccount().getLockedUntil();
            }
        }
    }

    public UserDTO(Account account) {
        if (account != null) {
            this.accountId = account.getAccountId();
            this.password = account.getPassword();
            this.email = account.getUsername();
            this.roleName = account.getRole() != null ? account.getRole().getRoleName() : null;
            this.accountStatus = account.getStatus();
            this.accountCreatedAt = account.getCreatedAt();
            this.lockedUntil = account.getLockedUntil();

            if (account.getEmployeeProfile() != null) {
                EmployeeProfile ep = account.getEmployeeProfile();
                this.employeeProfileId = ep.getEmployeeProfileId();
                this.fullName = ep.getFullName();
                this.gender = ep.getGender() != null ? (ep.getGender() ? "Nam" : "Nữ") : null;
                this.dateOfBirth = ep.getDateOfBirth();
                this.phoneNumber = ep.getPhoneNumber();
                this.email = ep.getEmail();
                this.avatarUrl = ep.getAvatarUrl();
                this.address = ep.getAddress();
            } else if (account.getProfile() != null) {
                Profile p = account.getProfile();
                this.profileId = p.getProfileId();
                this.fullName = p.getFullName();
                this.gender = p.getGender();
                this.dateOfBirth = p.getDateOfBirth();
                this.placeOfBirth = p.getPlaceOfBirth();
                this.citizenId = p.getCitizenId();
                this.citizenIdIssueDate = p.getCitizenIdIssueDate();
                this.citizenIdIssuePlace = p.getCitizenIdIssuePlace();
                this.nationality = p.getNationality();
                this.ethnicity = p.getEthnicity();
                this.phoneNumber = p.getPhoneNumber();
                this.email = p.getEmail();
                this.avatarUrl = p.getAvatarUrl();
                this.emergencyContactName = p.getEmergencyContactName();
                this.emergencyContactPhone = p.getEmergencyContactPhone();
                this.relationshipToOwner = p.getRelationshipToOwner();
                this.moveInDate = p.getMoveInDate();
                this.moveOutDate = p.getMoveOutDate();
                this.isHouseholdOwner = p.getIsHouseholdOwner();
                this.residentStatus = p.getResidentStatus();
                this.occupation = p.getOccupation();

                if (p.getApartment() != null) {
                    this.apartmentId = p.getApartment().getApartmentId();
                    this.apartmentNumber = p.getApartment().getApartmentNumber();
                }
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

    public Integer getEmployeeProfileId() {
        return employeeProfileId;
    }

    public void setEmployeeProfileId(Integer employeeProfileId) {
        this.employeeProfileId = employeeProfileId;
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

    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
