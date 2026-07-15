package com.quan.apartment_building_management_system.service.user.impl;

import com.quan.apartment_building_management_system.entity.EmployeeProfile;
import com.quan.apartment_building_management_system.repository.EmployeeProfileRepository;
import com.quan.apartment_building_management_system.service.user.EmployeeProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeProfileServiceImpl implements EmployeeProfileService {

    private final EmployeeProfileRepository employeeProfileRepository;

    public EmployeeProfileServiceImpl(EmployeeProfileRepository employeeProfileRepository) {
        this.employeeProfileRepository = employeeProfileRepository;
    }

    @Override
    public List<EmployeeProfile> findAll() {
        return employeeProfileRepository.findAll();
    }

    @Override
    public Optional<EmployeeProfile> findById(Integer id) {
        return employeeProfileRepository.findById(id);
    }

    @Override
    public EmployeeProfile save(EmployeeProfile employeeProfile) {
        return employeeProfileRepository.save(employeeProfile);
    }

    @Override
    public void deleteById(Integer id) {
        employeeProfileRepository.deleteById(id);
    }
}
