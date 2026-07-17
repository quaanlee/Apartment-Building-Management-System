package com.quan.apartment_building_management_system.service.system;

import com.quan.apartment_building_management_system.dto.systemlog.SystemLogViewDto;
import com.quan.apartment_building_management_system.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    long countLogsByRole(String roleName);

    Page<SystemLog> searchLogs(String search, String roleName, LocalDate fromDate, LocalDate toDate, Pageable pageable);

    Page<SystemLog> findFiltered(LocalDateTime fromDate, LocalDateTime toDate,
                                 String role, String action, String search, int page, int size);

    List<Object[]> countLogsByRoleWithFilters(LocalDateTime fromDate, LocalDateTime toDate, String role, String action, String search);

    void logSystemAction(String action, String entityType, Integer entityId, Object oldDto, Object newDto, String description);
}
