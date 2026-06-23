package com.quan.apartment_building_management_system.service.user;

import com.quan.apartment_building_management_system.entity.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProfileService {

    List<Profile> findAll();

    Optional<Profile> findById(Integer id);

    Optional<Profile> findByAccountId(Integer accountId);

    Optional<Profile> findByCitizenId(String citizenId);

    Profile save(Profile profile);

    void deleteById(Integer id);
}
