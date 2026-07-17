package com.quan.apartment_building_management_system.service.utility.impl;

import com.quan.apartment_building_management_system.entity.ServiceItem;
import com.quan.apartment_building_management_system.repository.ServiceItemRepository;
import com.quan.apartment_building_management_system.service.utility.ServiceItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ServiceItemServiceImpl implements ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public ServiceItemServiceImpl(ServiceItemRepository serviceItemRepository, com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.serviceItemRepository = serviceItemRepository;
        this.systemLogService = systemLogService;
    }

    @Override
    public List<ServiceItem> findAll() {
        return serviceItemRepository.findAll();
    }

    @Override
    public Optional<ServiceItem> findById(Integer id) {
        return serviceItemRepository.findById(id);
    }

    @Override
    public Optional<ServiceItem> findByServiceName(String serviceName) {
        return serviceItemRepository.findByServiceName(serviceName);
    }

    @Override
    public List<ServiceItem> findByServiceType(String serviceType) {
        return serviceItemRepository.findByServiceType(serviceType);
    }

    @Override
    @Transactional
    public ServiceItem save(ServiceItem serviceItem) {
        return serviceItemRepository.save(serviceItem);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        serviceItemRepository.deleteById(id);
    }

    @Override
    public List<ServiceItem> searchServices(String keyword, Boolean status) {
        String normalizedKeyword = removeAccents(keyword != null ? keyword.trim().toLowerCase() : "");
        return serviceItemRepository.findAll().stream()
                .filter(s -> {
                    if (normalizedKeyword.isEmpty()) return true;
                    String name = s.getServiceName() != null ? removeAccents(s.getServiceName().toLowerCase()) : "";
                    String type = s.getServiceType() != null ? removeAccents(s.getServiceType().toLowerCase()) : "";
                    return name.contains(normalizedKeyword) || type.contains(normalizedKeyword);
                })
                .filter(s -> status == null || s.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    private String removeAccents(String s) {
        if (s == null) return "";
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }
}
