package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.SystemLog;

import java.util.List;
import java.util.Optional;

public interface SystemLogService {

    List<SystemLog> findAll();

    Optional<SystemLog> findById(Long id);

    List<SystemLog> findByAccountId(Integer accountId);

    SystemLog save(SystemLog systemLog);

    void deleteById(Long id);
}
