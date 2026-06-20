package com.quan.apartment_building_management_system.service.impl;

import com.quan.apartment_building_management_system.entity.SystemLog;
import com.quan.apartment_building_management_system.repository.SystemLogRepository;
import com.quan.apartment_building_management_system.service.SystemLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogRepository systemLogRepository;

    public SystemLogServiceImpl(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    @Override
    public List<SystemLog> findAll() {
        return systemLogRepository.findAll();
    }

    @Override
    public Optional<SystemLog> findById(Long id) {
        return systemLogRepository.findById(id);
    }

    @Override
    public List<SystemLog> findByAccountId(Integer accountId) {
        return systemLogRepository.findByAccountAccountId(accountId);
    }

    @Override
    @Transactional
    public SystemLog save(SystemLog systemLog) {
        return systemLogRepository.save(systemLog);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        systemLogRepository.deleteById(id);
    }
}
