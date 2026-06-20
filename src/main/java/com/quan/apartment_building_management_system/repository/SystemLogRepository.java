package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    List<SystemLog> findByAccountAccountId(Integer accountId);
}
