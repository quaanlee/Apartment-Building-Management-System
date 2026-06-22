package com.quan.apartment_building_management_system.service.user;

import com.quan.apartment_building_management_system.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<Role> findAll();

    Optional<Role> findById(Integer id);

    Optional<Role> findByRoleName(String roleName);

    Role save(Role role);

    void deleteById(Integer id);
}
