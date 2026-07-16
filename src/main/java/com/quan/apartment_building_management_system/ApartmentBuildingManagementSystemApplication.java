package com.quan.apartment_building_management_system;

import com.quan.apartment_building_management_system.repository.MaintenanceTaskRepository;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceTaskService;
import com.quan.apartment_building_management_system.service.maintenance.impl.MaintenanceTaskServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApartmentBuildingManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApartmentBuildingManagementSystemApplication.class, args);
    }

    @Bean
    public MaintenanceTaskService maintenanceTaskService(MaintenanceTaskRepository repository) {
        return new MaintenanceTaskServiceImpl(repository);
    }
}

