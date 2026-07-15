package com.quan.apartment_building_management_system.service.admin;

import com.quan.apartment_building_management_system.dto.admin.AdminDashboardStatsDto;
import com.quan.apartment_building_management_system.repository.AccountRepository;
import com.quan.apartment_building_management_system.repository.ApartmentRepository;
import com.quan.apartment_building_management_system.repository.ServiceItemRepository;
import com.quan.apartment_building_management_system.repository.SystemLogRepository;
import com.quan.apartment_building_management_system.repository.UtilityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final AccountRepository accountRepository;
    private final ApartmentRepository apartmentRepository;
    private final UtilityRepository utilityRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final SystemLogRepository systemLogRepository;

    public AdminDashboardService(AccountRepository accountRepository, 
                                 ApartmentRepository apartmentRepository,
                                 UtilityRepository utilityRepository, 
                                 ServiceItemRepository serviceItemRepository,
                                 SystemLogRepository systemLogRepository) {
        this.accountRepository = accountRepository;
        this.apartmentRepository = apartmentRepository;
        this.utilityRepository = utilityRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.systemLogRepository = systemLogRepository;
    }

    public AdminDashboardStatsDto getDashboardStats() {
        AdminDashboardStatsDto stats = new AdminDashboardStatsDto();

        // Account stats
        stats.setTotalAccounts(accountRepository.count());
        stats.setActiveAccounts(accountRepository.countByStatus(true));

        // Apartment stats
        stats.setTotalApartments(apartmentRepository.count());
        stats.setOccupiedApartments(apartmentRepository.countByStatus((byte) 1));

        // Utility & Service stats
        stats.setTotalUtilities(utilityRepository.count());
        stats.setTotalServices(serviceItemRepository.count());

        // Recent Logs
        stats.setRecentLogs(systemLogRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent());

        return stats;
    }
}
