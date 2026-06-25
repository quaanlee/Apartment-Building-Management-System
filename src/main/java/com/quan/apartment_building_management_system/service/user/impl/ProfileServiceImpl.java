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

        // Save Account
        Account account = new Account();
        account.setUsername(userDto.getUsername());
        account.setPassword(userDto.getPassword());
        account.setRole(roleOpt.get());
        account.setStatus(userDto.getAccountStatus() != null ? userDto.getAccountStatus() : true);
        account = accountRepository.save(account);

        // Save Profile
        Profile profile = new Profile();
        profile.setAccount(account);
        profile.setFullName(userDto.getFullName());
        profile.setGender(userDto.getGender());
        profile.setDateOfBirth(userDto.getDateOfBirth());
        profile.setPlaceOfBirth(userDto.getPlaceOfBirth());
        profile.setCitizenId(userDto.getCitizenId());
        profile.setCitizenIdIssueDate(userDto.getCitizenIdIssueDate());
        profile.setCitizenIdIssuePlace(userDto.getCitizenIdIssuePlace());
        profile.setNationality(userDto.getNationality());
        profile.setEthnicity(userDto.getEthnicity());
        profile.setPhoneNumber(userDto.getPhoneNumber());
        profile.setEmail(userDto.getEmail());
        profile.setAvatarUrl(userDto.getAvatarUrl());
        profile.setEmergencyContactName(userDto.getEmergencyContactName());
        profile.setEmergencyContactPhone(userDto.getEmergencyContactPhone());

        // Handle role-specific attributes
        if ("RESIDENT".equalsIgnoreCase(userDto.getRoleName())) {
            profile.setIsHouseholdOwner(userDto.getIsHouseholdOwner() != null && userDto.getIsHouseholdOwner());
            profile.setResidentStatus(userDto.getResidentStatus() != null ? userDto.getResidentStatus() : 1);
            profile.setMoveInDate(userDto.getMoveInDate());
            profile.setMoveOutDate(userDto.getMoveOutDate());
        } else if ("MANAGER".equalsIgnoreCase(userDto.getRoleName()) || "MAINTENANCE_STAFF".equalsIgnoreCase(userDto.getRoleName())) {
            profile.setOccupation(userDto.getOccupation());
        }

        profile = profileRepository.save(profile);

        return new UserDTO(profile);
    }
}
