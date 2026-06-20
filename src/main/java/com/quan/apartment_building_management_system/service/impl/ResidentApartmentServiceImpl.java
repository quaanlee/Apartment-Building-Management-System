package com.quan.apartment_building_management_system.service.impl;

import com.quan.apartment_building_management_system.entity.ResidentApartment;
import com.quan.apartment_building_management_system.repository.ResidentApartmentRepository;
import com.quan.apartment_building_management_system.service.ResidentApartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ResidentApartmentServiceImpl implements ResidentApartmentService {

    private final ResidentApartmentRepository residentApartmentRepository;

    public ResidentApartmentServiceImpl(ResidentApartmentRepository residentApartmentRepository) {
        this.residentApartmentRepository = residentApartmentRepository;
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
        return residentApartmentRepository.save(residentApartment);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        residentApartmentRepository.deleteById(id);
    }
}
