package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Integer> {
}
