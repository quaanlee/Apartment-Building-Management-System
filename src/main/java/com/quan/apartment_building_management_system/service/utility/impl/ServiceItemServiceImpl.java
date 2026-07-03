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

    public ServiceItemServiceImpl(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
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
        return serviceItemRepository.findAll().stream()
                .filter(s -> keyword == null || keyword.isEmpty() ||
                        s.getServiceName().toLowerCase().contains(keyword.toLowerCase()) ||
                        (s.getServiceType() != null && s.getServiceType().toLowerCase().contains(keyword.toLowerCase())))
                .filter(s -> status == null || s.getStatus().equals(status))
                .collect(Collectors.toList());
    }
}
