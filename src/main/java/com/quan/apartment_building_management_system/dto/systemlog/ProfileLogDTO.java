package com.quan.apartment_building_management_system.dto.systemlog;

/**
 * DTO ghi log cho Profile (hồ sơ cư dân).
 * Dùng khi tạo mới hoặc cập nhật hồ sơ cư dân.
 */
public class ProfileLogDTO {
    private Integer profileId;
    private String fullName;
    private String gender;
    private String dateOfBirth;
    private String citizenId;
    private String phoneNumber;
    private String email;
    private String nationality;
    private String occupation;
    private String moveInDate;
    private String moveOutDate;
    private Boolean isHouseholdOwner;
    private Byte residentStatus;
    private Integer apartmentId;
    private String apartmentNumber;
    private String role;

    public ProfileLogDTO() {}

    public ProfileLogDTO(Integer profileId, String fullName, String gender, String dateOfBirth,
                         String citizenId, String phoneNumber, String email, String nationality,
                         String occupation, String moveInDate, String moveOutDate,
                         Boolean isHouseholdOwner, Byte residentStatus,
                         Integer apartmentId, String apartmentNumber, String role) {
        this.profileId = profileId;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.citizenId = citizenId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nationality = nationality;
        this.occupation = occupation;
        this.moveInDate = moveInDate;
        this.moveOutDate = moveOutDate;
        this.isHouseholdOwner = isHouseholdOwner;
        this.residentStatus = residentStatus;
        this.apartmentId = apartmentId;
        this.apartmentNumber = apartmentNumber;
        this.role = role;
    }

    public static ProfileLogDTO fromEntity(com.quan.apartment_building_management_system.entity.Profile profile) {
        if (profile == null) return new ProfileLogDTO();
        com.quan.apartment_building_management_system.entity.Apartment apt = profile.getApartment();
        String roleName = null;
        if (profile.getAccount() != null && profile.getAccount().getRole() != null) {
            roleName = profile.getAccount().getRole().getRoleName();
        }
        return new ProfileLogDTO(
                profile.getProfileId(),
                profile.getFullName(),
                profile.getGender(),
                profile.getDateOfBirth() != null ? profile.getDateOfBirth().toString() : null,
                profile.getCitizenId(),
                profile.getPhoneNumber(),
                profile.getEmail(),
                profile.getNationality(),
                profile.getOccupation(),
                profile.getMoveInDate() != null ? profile.getMoveInDate().toString() : null,
                profile.getMoveOutDate() != null ? profile.getMoveOutDate().toString() : null,
                profile.getIsHouseholdOwner(),
                profile.getResidentStatus(),
                apt != null ? apt.getApartmentId() : null,
                apt != null ? apt.getApartmentNumber() : null,
                roleName
        );
    }

    public static ProfileLogDTO fromUserDTO(com.quan.apartment_building_management_system.dto.user.UserDTO dto) {
        if (dto == null) return new ProfileLogDTO();
        return new ProfileLogDTO(
                dto.getProfileId(),
                dto.getFullName(),
                dto.getGender(),
                dto.getDateOfBirth() != null ? dto.getDateOfBirth().toString() : null,
                dto.getCitizenId(),
                dto.getPhoneNumber(),
                dto.getEmail(),
                dto.getNationality(),
                dto.getOccupation(),
                dto.getMoveInDate() != null ? dto.getMoveInDate().toString() : null,
                dto.getMoveOutDate() != null ? dto.getMoveOutDate().toString() : null,
                dto.getIsHouseholdOwner(),
                dto.getResidentStatus(),
                dto.getApartmentId(),
                dto.getApartmentNumber(),
                dto.getRoleName()
        );
    }

    public static ProfileLogDTO empty() { return new ProfileLogDTO(); }

    public Integer getProfileId() { return profileId; }
    public void setProfileId(Integer profileId) { this.profileId = profileId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getMoveInDate() { return moveInDate; }
    public void setMoveInDate(String moveInDate) { this.moveInDate = moveInDate; }
    public String getMoveOutDate() { return moveOutDate; }
    public void setMoveOutDate(String moveOutDate) { this.moveOutDate = moveOutDate; }
    public Boolean getIsHouseholdOwner() { return isHouseholdOwner; }
    public void setIsHouseholdOwner(Boolean isHouseholdOwner) { this.isHouseholdOwner = isHouseholdOwner; }
    public Byte getResidentStatus() { return residentStatus; }
    public void setResidentStatus(Byte residentStatus) { this.residentStatus = residentStatus; }
    public Integer getApartmentId() { return apartmentId; }
    public void setApartmentId(Integer apartmentId) { this.apartmentId = apartmentId; }
    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
