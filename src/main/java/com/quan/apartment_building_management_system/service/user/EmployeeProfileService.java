package com.quan.apartment_building_management_system.service.user;

import com.quan.apartment_building_management_system.entity.EmployeeProfile;

import java.util.List;
import java.util.Optional;

public interface EmployeeProfileService {
    List<EmployeeProfile> findAll();
    Optional<EmployeeProfile> findById(Integer id);
    Optional<EmployeeProfile> findByAccountId(Integer accountId);
    EmployeeProfile save(EmployeeProfile employeeProfile);
    void deleteById(Integer id);
}