package com.quan.apartment_building_management_system.service.user.impl;

import com.quan.apartment_building_management_system.dto.user.UserDTO;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.Role;
import com.quan.apartment_building_management_system.entity.EmployeeProfile;
import com.quan.apartment_building_management_system.repository.EmployeeProfileRepository;
import com.quan.apartment_building_management_system.repository.AccountRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import com.quan.apartment_building_management_system.repository.RoleRepository;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository,
            AccountRepository accountRepository,
            RoleRepository roleRepository,
            EmployeeProfileRepository employeeProfileRepository) {
        this.profileRepository = profileRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    @Override
    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    @Override
    public Optional<Profile> findById(Integer id) {
        return profileRepository.findById(id);
    }

    @Override
    public Optional<Profile> findByAccountId(Integer accountId) {
        return profileRepository.findByAccountAccountId(accountId);
    }

    @Override
    public Optional<Profile> findByCitizenId(String citizenId) {
        return profileRepository.findByCitizenId(citizenId);
    }

    @Override
    @Transactional
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        profileRepository.deleteById(id);
    }

    @Override
    public Page<UserDTO> findFiltered(String search, Integer roleId, Boolean status, Pageable pageable) {
        return profileRepository.findFiltered(search, roleId, status, pageable).map(UserDTO::new);
    }

    @Override
    public List<Profile> findActiveMaintenanceStaffs() {
        return profileRepository.findActiveMaintenanceStaffs();
    }

    @Override
    @Transactional
    public UserDTO saveUserDTO(UserDTO userDto) {
        // Find role robustly (handles space/underscore formats case-insensitively)
        String lookupName = userDto.getRoleName();
        Optional<Role> roleOpt = roleRepository.findByRoleName(lookupName);
        if (!roleOpt.isPresent()) {
            roleOpt = roleRepository.findByRoleName(lookupName.toUpperCase());
        }
        if (!roleOpt.isPresent() && lookupName.contains("_")) {
            roleOpt = roleRepository.findByRoleName(lookupName.replace("_", " "));
        }
        if (!roleOpt.isPresent() && lookupName.contains(" ")) {
            roleOpt = roleRepository.findByRoleName(lookupName.replace(" ", "_"));
        }
        
        if (!roleOpt.isPresent()) {
            throw new IllegalArgumentException("Role not found: " + userDto.getRoleName());
        }

        boolean isEmployee = "MANAGER".equalsIgnoreCase(userDto.getRoleName()) 
                || "MAINTENANCE_STAFF".equalsIgnoreCase(userDto.getRoleName())
                || "MAINTENANCE STAFF".equalsIgnoreCase(userDto.getRoleName());

        Account account;
        Profile profile = null;
        EmployeeProfile employeeProfile = null;

        if (isEmployee) {
            if (userDto.getEmployeeProfileId() != null) {
                employeeProfile = employeeProfileRepository.findById(userDto.getEmployeeProfileId())
                        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("EmployeeProfile not found with ID: " + userDto.getEmployeeProfileId()));
                account = employeeProfile.getAccount();
                if (account == null) account = new Account();
            } else {
                employeeProfile = new EmployeeProfile();
                account = new Account();
            }
        } else {
            if (userDto.getProfileId() != null) {
                profile = profileRepository.findById(userDto.getProfileId())
                        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Profile not found with ID: " + userDto.getProfileId()));
                account = profile.getAccount();
                if (account == null) account = new Account();
            } else {
                profile = new Profile();
                account = new Account();
            }
        }

        // Save/Update Account
        // Keep username and role unchangeable in edit mode
        if (userDto.getProfileId() == null && userDto.getEmployeeProfileId() == null) {
            account.setUsername(userDto.getEmail());
            account.setRole(roleOpt.get());
        }
        account.setPassword(userDto.getPassword());
        account.setStatus(userDto.getAccountStatus() != null ? userDto.getAccountStatus() : true);
        account = accountRepository.save(account);

        if (isEmployee) {
            employeeProfile.setAccount(account);
            employeeProfile.setFullName(userDto.getFullName());
            employeeProfile.setGender(userDto.getGender() != null && "Nam".equalsIgnoreCase(userDto.getGender()));
            employeeProfile.setDateOfBirth(userDto.getDateOfBirth());
            employeeProfile.setPhoneNumber(userDto.getPhoneNumber());
            employeeProfile.setEmail(userDto.getEmail());
            employeeProfile.setAvatarUrl(userDto.getAvatarUrl());
            employeeProfile.setAddress(userDto.getAddress());
            employeeProfile.setStatus(userDto.getAccountStatus() != null ? userDto.getAccountStatus() : true);
            employeeProfile = employeeProfileRepository.save(employeeProfile);
            return new UserDTO(employeeProfile);
        } else {
            profile.setAccount(account);
            profile.setFullName(userDto.getFullName());
            profile.setGender(userDto.getGender());
            profile.setDateOfBirth(userDto.getDateOfBirth());
            profile.setPlaceOfBirth(userDto.getPlaceOfBirth());
            profile.setCitizenId(userDto.getCitizenId());
            profile.setCitizenIdIssueDate(userDto.getCitizenIdIssueDate());
            profile.setCitizenIdIssuePlace(userDto.getCitizenIdIssuePlace());
            profile.setNationality(userDto.getNationality() != null ? userDto.getNationality() : "Vietnam");
            profile.setEthnicity(userDto.getEthnicity() != null ? userDto.getEthnicity() : "Kinh");
            profile.setPhoneNumber(userDto.getPhoneNumber());
            profile.setEmail(userDto.getEmail());
            profile.setAvatarUrl(userDto.getAvatarUrl());
            profile.setEmergencyContactName(userDto.getEmergencyContactName());
            profile.setEmergencyContactPhone(userDto.getEmergencyContactPhone());

            profile.setRelationshipToOwner(userDto.getRelationshipToOwner());
            profile.setIsHouseholdOwner(userDto.getIsHouseholdOwner() != null && userDto.getIsHouseholdOwner());
            profile.setResidentStatus(userDto.getResidentStatus() != null ? userDto.getResidentStatus() : 1);
            profile.setMoveInDate(userDto.getMoveInDate());
            profile.setMoveOutDate(userDto.getMoveOutDate());
            profile.setOccupation(null); // Clear occupation just in case

            profile = profileRepository.save(profile);
            return new UserDTO(profile);
        }
    }
}
