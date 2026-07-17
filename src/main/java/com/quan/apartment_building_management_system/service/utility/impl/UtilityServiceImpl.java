package com.quan.apartment_building_management_system.service.utility.impl;

import com.quan.apartment_building_management_system.entity.Utility;
import com.quan.apartment_building_management_system.repository.UtilityRepository;
import com.quan.apartment_building_management_system.service.utility.UtilityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UtilityServiceImpl implements UtilityService {

    private final UtilityRepository utilityRepository;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public UtilityServiceImpl(UtilityRepository utilityRepository, com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.utilityRepository = utilityRepository;
        this.systemLogService = systemLogService;
    }

    @Override
    public List<Utility> findAll() {
        return utilityRepository.findAll();
    }

    @Override
    public Optional<Utility> findById(Integer id) {
        return utilityRepository.findById(id);
    }

    @Override
    public Optional<Utility> findByUtilityName(String utilityName) {
        return utilityRepository.findByUtilityName(utilityName);
    }

    @Override
    @Transactional
    public Utility save(Utility utility) {
        return utilityRepository.save(utility);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        utilityRepository.deleteById(id);
    }

    @Override
    public List<Utility> searchUtilities(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        String normalizedQuery = removeAccents(query.trim().toLowerCase());
        return findAll().stream()
                .filter(u -> {
                    String name = u.getUtilityName() != null ? removeAccents(u.getUtilityName().toLowerCase()) : "";
                    String desc = u.getDescription() != null ? removeAccents(u.getDescription().toLowerCase()) : "";
                    return name.contains(normalizedQuery) || desc.contains(normalizedQuery);
                })
                .toList();
    }

    private String removeAccents(String s) {
        if (s == null) return "";
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }
}
