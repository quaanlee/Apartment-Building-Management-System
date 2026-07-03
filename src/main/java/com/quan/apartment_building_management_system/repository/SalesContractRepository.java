package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.SalesContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesContractRepository extends JpaRepository<SalesContract, Integer> {
}
