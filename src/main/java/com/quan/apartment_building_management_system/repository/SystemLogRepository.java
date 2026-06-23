package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    List<SystemLog> findByAccountAccountId(Integer accountId);
}
