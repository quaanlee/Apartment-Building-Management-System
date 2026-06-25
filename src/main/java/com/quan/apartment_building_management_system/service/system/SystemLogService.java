package com.quan.apartment_building_management_system.service.system;

import com.quan.apartment_building_management_system.dto.SystemLogViewDto;
import com.quan.apartment_building_management_system.entity.SystemLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SystemLogService {

    List<SystemLog> findAll();

    Optional<SystemLog> findById(Long id);

    List<SystemLog> findByAccountId(Integer accountId);

    SystemLog save(SystemLog systemLog);

    void deleteById(Long id);

    List<SystemLogViewDto> getSystemLogs(LocalDate fromDate, LocalDate toDate, String role);

    long countTotalEvents();
}
