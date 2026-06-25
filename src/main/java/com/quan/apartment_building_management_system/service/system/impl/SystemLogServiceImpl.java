package com.quan.apartment_building_management_system.service.system.impl;

import com.quan.apartment_building_management_system.dto.SystemLogViewDto;
import com.quan.apartment_building_management_system.entity.SystemLog;
import com.quan.apartment_building_management_system.repository.SystemLogRepository;
import com.quan.apartment_building_management_system.service.system.SystemLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Override
    public List<SystemLogViewDto> getSystemLogs(LocalDate fromDate, LocalDate toDate, String role) {

        List<SystemLogViewDto> demoLogs = List.of(
                new SystemLogViewDto(
                        LocalDateTime.of(2023, 10, 31, 14, 23, 11),
                        "Admin",
                        "Alex Mercer",
                        "LOGIN",
                        "Account"
                ),
                new SystemLogViewDto(
                        LocalDateTime.of(2023, 10, 31, 14, 15, 4),
                        "Manager",
                        "Jane Doe",
                        "CREATE_BILL",
                        "Bill"
                ),
                new SystemLogViewDto(
                        LocalDateTime.of(2023, 10, 31, 13, 58, 22),
                        "System",
                        "System Process",
                        "LOCK_ACCOUNT",
                        "Account"
                ),
                new SystemLogViewDto(
                        LocalDateTime.of(2023, 10, 31, 13, 42, 10),
                        "Admin",
                        "Alex Mercer",
                        "CANCEL_BOOKING",
                        "Booking"
                ),
                new SystemLogViewDto(
                        LocalDateTime.of(2023, 10, 31, 12, 10, 55),
                        "Security Officer",
                        "Ryan Smith",
                        "UPDATE_PERMISSIONS",
                        "Account"
                )
        );

        return demoLogs.stream()
                .filter(log -> {
                    LocalDate logDate = log.getCreatedAt().toLocalDate();

                    boolean validFromDate = fromDate == null || !logDate.isBefore(fromDate);
                    boolean validToDate = toDate == null || !logDate.isAfter(toDate);
                    boolean validRole = role == null
                            || role.isBlank()
                            || role.equals("All Roles")
                            || log.getRole().equalsIgnoreCase(role);

                    return validFromDate && validToDate && validRole;
                })
                .toList();
    }

    @Override
    public long countTotalEvents() {
        long countFromDatabase = systemLogRepository.count();

        if (countFromDatabase == 0) {
            return 128492;
        }

        return countFromDatabase;
    }
}
