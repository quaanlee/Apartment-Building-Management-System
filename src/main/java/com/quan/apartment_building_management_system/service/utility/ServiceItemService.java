package com.quan.apartment_building_management_system.service.utility;

import com.quan.apartment_building_management_system.entity.ServiceItem;

import java.util.List;
import java.util.Optional;

public interface ServiceItemService {

    List<ServiceItem> findAll();

    Optional<ServiceItem> findById(Integer id);

    Optional<ServiceItem> findByServiceName(String serviceName);

    List<ServiceItem> findByServiceType(String serviceType);

    ServiceItem save(ServiceItem serviceItem);

    void deleteById(Integer id);

    List<ServiceItem> searchServices(String keyword, Boolean status);
}
