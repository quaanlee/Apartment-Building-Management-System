package com.quan.apartment_building_management_system;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.Role;
import com.quan.apartment_building_management_system.entity.SystemLog;
import com.quan.apartment_building_management_system.repository.AccountRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import com.quan.apartment_building_management_system.repository.RoleRepository;
import com.quan.apartment_building_management_system.repository.SystemLogRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final SystemLogRepository systemLogRepository;

    public DataInitializer(RoleRepository roleRepository,
                           AccountRepository accountRepository,
                           ProfileRepository profileRepository,
                           SystemLogRepository systemLogRepository) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.systemLogRepository = systemLogRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Initialize Roles if empty
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setRoleName("Admin");

            Role managerRole = new Role();
            managerRole.setRoleName("Manager");

            Role securityRole = new Role();
            securityRole.setRoleName("Security Officer");

            Role residentRole = new Role();
            residentRole.setRoleName("Resident");

            Role maintenanceRole = new Role();
            maintenanceRole.setRoleName("Maintenance Staff");

            roleRepository.saveAll(Arrays.asList(adminRole, managerRole, securityRole, residentRole, maintenanceRole));
        }

        // 2. Initialize Accounts and Profiles if empty
        if (accountRepository.count() == 0) {
            Role admin = roleRepository.findAll().stream().filter(r -> r.getRoleName().equals("Admin")).findFirst().orElse(null);
            Role manager = roleRepository.findAll().stream().filter(r -> r.getRoleName().equals("Manager")).findFirst().orElse(null);
            Role security = roleRepository.findAll().stream().filter(r -> r.getRoleName().equals("Security Officer")).findFirst().orElse(null);

            // Create Alex Mercer (Admin)
            Account accAdmin = new Account();
            accAdmin.setUsername("alex.mercer");
            accAdmin.setPassword("password123");
            accAdmin.setRole(admin);
            accAdmin = accountRepository.save(accAdmin);

            Profile profAdmin = new Profile();
            profAdmin.setAccount(accAdmin);
            profAdmin.setFullName("Alex Mercer");
            profileRepository.save(profAdmin);

            // Create Jane Doe (Manager)
            Account accManager = new Account();
            accManager.setUsername("jane.doe");
            accManager.setPassword("password123");
            accManager.setRole(manager);
            accManager = accountRepository.save(accManager);

            Profile profManager = new Profile();
            profManager.setAccount(accManager);
            profManager.setFullName("Jane Doe");
            profileRepository.save(profManager);

            // Create Ryan Smith (Security Officer)
            Account accSecurity = new Account();
            accSecurity.setUsername("ryan.smith");
            accSecurity.setPassword("password123");
            accSecurity.setRole(security);
            accSecurity = accountRepository.save(accSecurity);

            Profile profSecurity = new Profile();
            profSecurity.setAccount(accSecurity);
            profSecurity.setFullName("Ryan Smith");
            profileRepository.save(profSecurity);
        }

        // 3. Initialize SystemLogs matching the screenshot if empty
        if (systemLogRepository.count() == 0) {
            Account alex = accountRepository.findAll().stream().filter(a -> a.getUsername().equals("alex.mercer")).findFirst().orElse(null);
            Account jane = accountRepository.findAll().stream().filter(a -> a.getUsername().equals("jane.doe")).findFirst().orElse(null);
            Account ryan = accountRepository.findAll().stream().filter(a -> a.getUsername().equals("ryan.smith")).findFirst().orElse(null);

            // Mockup dates are set around October 31, 2023. Let's create dates around there as well as recent dates.
            // Log 1: Alex Mercer (Admin) - LOGIN
            SystemLog log1 = new SystemLog();
            log1.setAccount(alex);
            log1.setAction("LOGIN");
            log1.setEntityType("Account");
            log1.setEntityId(alex != null ? alex.getAccountId() : 1);
            log1.setIpAddress("192.168.1.100");
            log1.setCreatedAt(LocalDateTime.of(2023, 10, 31, 14, 22, 11));
            systemLogRepository.save(log1);

            // Log 2: Jane Doe (Manager) - CREATE_BILL
            SystemLog log2 = new SystemLog();
            log2.setAccount(jane);
            log2.setAction("CREATE_BILL");
            log2.setEntityType("Bill");
            log2.setEntityId(101);
            log2.setIpAddress("192.168.1.102");
            log2.setCreatedAt(LocalDateTime.of(2023, 10, 31, 14, 15, 4));
            systemLogRepository.save(log2);

            // Log 3: System Process - LOCK_ACCOUNT
            SystemLog log3 = new SystemLog();
            log3.setAccount(null); // NULL represents system process
            log3.setAction("LOCK_ACCOUNT");
            log3.setEntityType("Account");
            log3.setEntityId(12);
            log3.setIpAddress("127.0.0.1");
            log3.setCreatedAt(LocalDateTime.of(2023, 10, 31, 13, 58, 22));
            systemLogRepository.save(log3);

            // Log 4: Alex Mercer (Admin) - CANCEL_BOOKING
            SystemLog log4 = new SystemLog();
            log4.setAccount(alex);
            log4.setAction("CANCEL_BOOKING");
            log4.setEntityType("Booking");
            log4.setEntityId(205);
            log4.setIpAddress("192.168.1.100");
            log4.setCreatedAt(LocalDateTime.of(2023, 10, 31, 12, 42, 10));
            systemLogRepository.save(log4);

            // Log 5: Ryan Smith (Security Officer) - UPDATE_PERMISSIONS
            SystemLog log5 = new SystemLog();
            log5.setAccount(ryan);
            log5.setAction("UPDATE_PERMISSIONS");
            log5.setEntityType("Account");
            log5.setEntityId(3);
            log5.setIpAddress("192.168.1.105");
            log5.setCreatedAt(LocalDateTime.of(2023, 10, 31, 12, 10, 55));
            systemLogRepository.save(log5);

            // Let's create some more historical logs to make pagination work (total 35 logs)
            for (int i = 1; i <= 30; i++) {
                SystemLog log = new SystemLog();
                // Alternate accounts
                if (i % 3 == 0) {
                    log.setAccount(alex);
                    log.setAction("UPDATE_APARTMENT");
                    log.setEntityType("Apartment");
                    log.setEntityId(15);
                    log.setIpAddress("192.168.1.100");
                } else if (i % 3 == 1) {
                    log.setAccount(jane);
                    log.setAction("APPROVE_VEHICLE");
                    log.setEntityType("Vehicle");
                    log.setEntityId(50 + i);
                    log.setIpAddress("192.168.1.102");
                } else {
                    log.setAccount(null); // System
                    log.setAction("DAILY_BACKUP");
                    log.setEntityType("System");
                    log.setEntityId(0);
                    log.setIpAddress("127.0.0.1");
                }
                
                // Spread dates between Oct 20, 2023 and Oct 30, 2023
                log.setCreatedAt(LocalDateTime.of(2023, 10, 20, 8, 0, 0).plusHours(i * 7).plusMinutes(i * 13));
                systemLogRepository.save(log);
            }
        }
    }
}
