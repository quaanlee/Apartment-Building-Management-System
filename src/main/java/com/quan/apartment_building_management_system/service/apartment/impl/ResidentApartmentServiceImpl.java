package com.quan.apartment_building_management_system.service.apartment.impl;

import com.quan.apartment_building_management_system.entity.ResidentApartment;
import com.quan.apartment_building_management_system.repository.ResidentApartmentRepository;
import com.quan.apartment_building_management_system.service.apartment.ResidentApartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ResidentApartmentServiceImpl implements ResidentApartmentService {

    private final ResidentApartmentRepository residentApartmentRepository;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public ResidentApartmentServiceImpl(ResidentApartmentRepository residentApartmentRepository, com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.residentApartmentRepository = residentApartmentRepository;
        this.systemLogService = systemLogService;
    }

    @Override
    public List<ResidentApartment> findAll() {
        return residentApartmentRepository.findAll();
    }

    @Override
    public Optional<ResidentApartment> findById(Integer id) {
        return residentApartmentRepository.findById(id);
    }

    @Override
    public List<ResidentApartment> findByProfileId(Integer profileId) {
        return residentApartmentRepository.findByProfileProfileId(profileId);
    }

    @Override
    public List<ResidentApartment> findByApartmentId(Integer apartmentId) {
        return residentApartmentRepository.findByApartmentApartmentId(apartmentId);
    }

    @Override
    @Transactional
    public ResidentApartment save(ResidentApartment residentApartment) {
        boolean isNew = residentApartment.getResidentApartmentId() == null;
        ResidentApartment saved = residentApartmentRepository.save(residentApartment);
        
        String action = isNew ? "ADD_RESIDENT" : "UPDATE_RESIDENT_APARTMENT";
        String aptNum = saved.getApartment() != null ? saved.getApartment().getApartmentNumber() : "Unknown";
        String residentName = saved.getProfile() != null ? saved.getProfile().getFullName() : "Unknown";
        String desc = isNew ? "Added resident " + residentName + " to apartment " + aptNum : "Updated resident " + residentName + " in apartment " + aptNum;
        
        systemLogService.logSystemAction(action, "Apartment", saved.getApartment() != null ? saved.getApartment().getApartmentId() : null, null, null, desc);
        
        return saved;
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        residentApartmentRepository.deleteById(id);
    }
}
