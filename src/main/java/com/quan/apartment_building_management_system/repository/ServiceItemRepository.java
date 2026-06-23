package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Integer> {

    Optional<ServiceItem> findByServiceName(String serviceName);

    List<ServiceItem> findByServiceType(String serviceType);
}
