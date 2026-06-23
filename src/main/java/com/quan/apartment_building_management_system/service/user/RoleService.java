package com.quan.apartment_building_management_system.service.user;

import com.quan.apartment_building_management_system.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface RoleService {

    List<Role> findAll();

    Optional<Role> findById(Integer id);

    Optional<Role> findByRoleName(String roleName);

    Role save(Role role);

    void deleteById(Integer id);
}
