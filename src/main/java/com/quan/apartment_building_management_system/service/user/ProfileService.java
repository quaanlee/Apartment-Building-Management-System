package com.quan.apartment_building_management_system.service.user;

import com.quan.apartment_building_management_system.dto.user.UserDTO;
import com.quan.apartment_building_management_system.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProfileService {

    List<Profile> findAll();

    Optional<Profile> findById(Integer id);

    Optional<Profile> findByAccountId(Integer accountId);

    Optional<Profile> findByCitizenId(String citizenId);

    Profile save(Profile profile);

    void deleteById(Integer id);

    Page<UserDTO> findFiltered(String search, Integer roleId, Boolean status, Pageable pageable);
    List<Profile> findActiveMaintenanceStaffs();
    UserDTO saveUserDTO(UserDTO userDto);
    Optional<Profile> findByPhoneNumber(String phoneNumber);
}
