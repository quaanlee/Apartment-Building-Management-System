package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Integer> {

    Optional<ServiceItem> findByServiceName(String serviceName);

    List<ServiceItem> findByServiceType(String serviceType);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM ServiceItem s JOIN FETCH s.unit WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(s.serviceName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.serviceType) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR s.status = :status)")
    List<ServiceItem> searchServices(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("status") Boolean status
    );
}
