package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByRoleName(String roleName);
}
