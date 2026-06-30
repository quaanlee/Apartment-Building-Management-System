package com.quan.apartment_building_management_system.service.user.impl;

import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
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
    public List<Profile> findActiveMaintenanceStaffs() {
        return profileRepository.findActiveMaintenanceStaffs();
    }
}
