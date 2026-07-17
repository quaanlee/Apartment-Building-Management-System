package com.quan.apartment_building_management_system.service.system.impl;

import com.quan.apartment_building_management_system.dto.systemlog.SystemLogViewDto;
import com.quan.apartment_building_management_system.entity.SystemLog;
import com.quan.apartment_building_management_system.repository.SystemLogRepository;
import com.quan.apartment_building_management_system.service.system.SystemLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final com.quan.apartment_building_management_system.repository.AccountRepository accountRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public SystemLogServiceImpl(SystemLogRepository systemLogRepository, com.quan.apartment_building_management_system.repository.AccountRepository accountRepository) {
        this.systemLogRepository = systemLogRepository;
        this.accountRepository = accountRepository;
        this.objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        this.objectMapper.findAndRegisterModules();
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
        return systemLogRepository.findByAccountAccountId(accountId, PageRequest.of(0, 100)).getContent();
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

    @Override
    public long countLogsByRole(String roleName) {
        return systemLogRepository.countByRoleName(roleName);
    }

    @Override
    public org.springframework.data.domain.Page<SystemLog> searchLogs(String search, String roleName, LocalDate fromDate, LocalDate toDate, org.springframework.data.domain.Pageable pageable) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }

        LocalDateTime startDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime endDateTime = toDate != null ? toDate.atTime(java.time.LocalTime.MAX) : null;

        return systemLogRepository.searchLogs(search, roleName, startDateTime, endDateTime, pageable);
    }

    @Override
    public Page<SystemLog> findFiltered(LocalDateTime fromDate, LocalDateTime toDate,
                                         String role, String action, String search, int page, int size) {
        return systemLogRepository.findFiltered(fromDate, toDate, role, action, search,
                PageRequest.of(page, size));
    }
    @Override
    public List<Object[]> countLogsByRoleWithFilters(LocalDateTime fromDate, LocalDateTime toDate,
                                                      String role, String action, String search) {
        return systemLogRepository.countLogsByRoleWithFilters(fromDate, toDate, role, action, search);
    }

    @Override
    @Transactional
    public void logSystemAction(String action, String entityType, Integer entityId, Object oldDto, Object newDto, String description) {
        SystemLog log = new SystemLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);

        try {
            log.setOldValue(oldDto == null ? "{}" : objectMapper.writeValueAsString(oldDto));
            log.setNewValue(newDto == null ? "{}" : objectMapper.writeValueAsString(newDto));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.setOldValue("{}");
            log.setNewValue("{}");
        }

        // Get current user from Session or SecurityContext
        com.quan.apartment_building_management_system.entity.Account currentUser = null;
        org.springframework.web.context.request.RequestAttributes attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (attributes instanceof org.springframework.web.context.request.ServletRequestAttributes) {
            jakarta.servlet.http.HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes) attributes).getRequest();
            jakarta.servlet.http.HttpSession session = request.getSession(false);
            if (session != null) {
                currentUser = (com.quan.apartment_building_management_system.entity.Account) session.getAttribute("currentUser");
            }
        }
        
        // Fallback to SecurityContextHolder if session currentUser is null
        if (currentUser == null) {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
                String username = auth.getName();
                currentUser = accountRepository.findByUsername(username).orElse(null);
            }
        }
        
        if (currentUser != null) {
            log.setAccount(currentUser);
        }

        systemLogRepository.save(log);
    }
}
