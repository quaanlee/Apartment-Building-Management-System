package com.quan.apartment_building_management_system.service.user.impl;

import com.quan.apartment_building_management_system.dto.UserDTO;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.Role;
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

    public ProfileServiceImpl(ProfileRepository profileRepository, 
                              AccountRepository accountRepository, 
                              RoleRepository roleRepository) {
        this.profileRepository = profileRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
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
    @Transactional
    public UserDTO saveUserDTO(UserDTO userDto) {
        // Find role
        Optional<Role> roleOpt = roleRepository.findByRoleName(userDto.getRoleName().toUpperCase());
        if (!roleOpt.isPresent()) {
            throw new IllegalArgumentException("Role not found: " + userDto.getRoleName());
        }

        Account account;
        Profile profile;

        if (userDto.getProfileId() != null) {
            // Edit / Update Mode
            profile = profileRepository.findById(userDto.getProfileId())
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Profile not found with ID: " + userDto.getProfileId()));
            account = profile.getAccount();
            if (account == null) {
                account = new Account();
            }
        } else {
            // Create Mode
            profile = new Profile();
            account = new Account();
        }

        // Save/Update Account
        // Keep username and role unchangeable in edit mode
        if (userDto.getProfileId() == null) {
            account.setUsername(userDto.getUsername());
            account.setRole(roleOpt.get());
        }
        account.setPassword(userDto.getPassword());
        account.setStatus(userDto.getAccountStatus() != null ? userDto.getAccountStatus() : true);
        account = accountRepository.save(account);

        // Save/Update Profile
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

        // Handle role-specific attributes
        if ("RESIDENT".equalsIgnoreCase(userDto.getRoleName())) {
            profile.setRelationshipToOwner(userDto.getRelationshipToOwner());
            profile.setIsHouseholdOwner(userDto.getIsHouseholdOwner() != null && userDto.getIsHouseholdOwner());
            profile.setResidentStatus(userDto.getResidentStatus() != null ? userDto.getResidentStatus() : 1);
            profile.setMoveInDate(userDto.getMoveInDate());
            profile.setMoveOutDate(userDto.getMoveOutDate());
            profile.setOccupation(null); // Clear occupation just in case
        } else if ("MANAGER".equalsIgnoreCase(userDto.getRoleName()) || "MAINTENANCE_STAFF".equalsIgnoreCase(userDto.getRoleName())) {
            profile.setOccupation(userDto.getOccupation());
            profile.setIsHouseholdOwner(false);
            profile.setResidentStatus((byte) 1);
            profile.setMoveInDate(null);
            profile.setMoveOutDate(null);
        }

        profile = profileRepository.save(profile);

        return new UserDTO(profile);
    }
}
