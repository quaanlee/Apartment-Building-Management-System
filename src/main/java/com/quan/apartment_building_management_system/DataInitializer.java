package com.quan.apartment_building_management_system;

import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.*;
import com.quan.apartment_building_management_system.util.DatabaseConstraintFixer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final SystemLogRepository systemLogRepository;
    private final UnitRepository unitRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final ApartmentRepository apartmentRepository;
    private final ResidentApartmentRepository residentApartmentRepository;
    private final BillRepository billRepository;
    private final BillDetailRepository billDetailRepository;
    private final MaintenanceRequestRepository requestRepo;
    private final MaintenanceTaskRepository taskRepo;
    private final MaintenanceReportRepository reportRepo;
    private final DatabaseConstraintFixer databaseConstraintFixer;

    public DataInitializer(RoleRepository roleRepository,
                           AccountRepository accountRepository,
                           ProfileRepository profileRepository,
                           SystemLogRepository systemLogRepository,
                           UnitRepository unitRepository,
                           ServiceItemRepository serviceItemRepository,
                           ApartmentRepository apartmentRepository,
                           ResidentApartmentRepository residentApartmentRepository,
                           BillRepository billRepository,
                           BillDetailRepository billDetailRepository,
                           MaintenanceRequestRepository requestRepo,
                           MaintenanceTaskRepository taskRepo,
                           MaintenanceReportRepository reportRepo,
                           DatabaseConstraintFixer databaseConstraintFixer) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.systemLogRepository = systemLogRepository;
        this.unitRepository = unitRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.apartmentRepository = apartmentRepository;
        this.residentApartmentRepository = residentApartmentRepository;
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
        this.requestRepo = requestRepo;
        this.taskRepo = taskRepo;
        this.reportRepo = reportRepo;
        this.databaseConstraintFixer = databaseConstraintFixer;
    }

    @Override
    public void run(String... args) {
        // Fix UNIQUE constraints on nullable columns first
        databaseConstraintFixer.fixConstraints();

        System.out.println("=== DEBUGINFO: ALL SYSTEM ACCOUNTS IN DATABASE ===");
        try {
            accountRepository.findAll().forEach(acc -> {
                System.out.println("[ACCOUNT] Username: '" + acc.getUsername() + "' | Password: '" + acc.getPassword() + "'");
            });
        } catch (Exception e) {
            System.out.println("Failed to print accounts: " + e.getMessage());
        }
        System.out.println("=================================================");

        // 1. Initialize Roles
        if (roleRepository.count() == 0) {
            Role adminRole = new Role(); adminRole.setRoleName("Admin");
            Role managerRole = new Role(); managerRole.setRoleName("Manager");
            Role securityRole = new Role(); securityRole.setRoleName("Security Officer");
            Role residentRole = new Role(); residentRole.setRoleName("Resident");
            Role maintenanceRole = new Role(); maintenanceRole.setRoleName("Maintenance Staff");
            roleRepository.saveAll(Arrays.asList(adminRole, managerRole, securityRole, residentRole, maintenanceRole));
        }

        // 2. Initialize Accounts and Profiles
        if (accountRepository.count() == 0) {
            Role admin = roleRepository.findAll().stream().filter(r -> r.getRoleName().equals("Admin")).findFirst().orElse(null);
            Role manager = roleRepository.findAll().stream().filter(r -> r.getRoleName().equals("Manager")).findFirst().orElse(null);
            Role security = roleRepository.findAll().stream().filter(r -> r.getRoleName().equals("Security Officer")).findFirst().orElse(null);
            Role resident = roleRepository.findAll().stream().filter(r -> r.getRoleName().equals("Resident")).findFirst().orElse(null);

            Account accAdmin = new Account(); accAdmin.setUsername("alex.mercer"); accAdmin.setPassword("password123"); accAdmin.setRole(admin);
            accAdmin = accountRepository.save(accAdmin);
            Profile profAdmin = new Profile(); profAdmin.setAccount(accAdmin); profAdmin.setFullName("Alex Mercer");
            profileRepository.save(profAdmin);

            Account accManager = new Account(); accManager.setUsername("jane.doe"); accManager.setPassword("password123"); accManager.setRole(manager);
            accManager = accountRepository.save(accManager);
            Profile profManager = new Profile(); profManager.setAccount(accManager); profManager.setFullName("Jane Doe");
            profileRepository.save(profManager);

            Account accSecurity = new Account(); accSecurity.setUsername("ryan.smith"); accSecurity.setPassword("password123"); accSecurity.setRole(security);
            accSecurity = accountRepository.save(accSecurity);
            Profile profSecurity = new Profile(); profSecurity.setAccount(accSecurity); profSecurity.setFullName("Ryan Smith");
            profileRepository.save(profSecurity);
        }

        // 3. Initialize Units & ServiceItems
        if (unitRepository.count() == 0) {
            Unit unitArea = new Unit(); unitArea.setUnitName("Area"); unitRepository.save(unitArea);
            Unit unitService = new Unit(); unitService.setUnitName("Service"); unitRepository.save(unitService);

            ServiceItem rent = new ServiceItem(); rent.setServiceName("Rent Fee"); rent.setServiceType("RENT"); rent.setUnitPrice(new BigDecimal("5000000")); rent.setUnit(unitArea); rent.setStatus(true);
            ServiceItem electricity = new ServiceItem(); electricity.setServiceName("Electricity"); electricity.setServiceType("ELECTRICITY"); electricity.setUnitPrice(new BigDecimal("3500")); electricity.setUnit(unitService); electricity.setStatus(true);
            ServiceItem water = new ServiceItem(); water.setServiceName("Water"); water.setServiceType("WATER"); water.setUnitPrice(new BigDecimal("15000")); water.setUnit(unitService); water.setStatus(true);
            ServiceItem parking = new ServiceItem(); parking.setServiceName("Parking"); parking.setServiceType("PARKING"); parking.setUnitPrice(new BigDecimal("500000")); parking.setUnit(unitService); parking.setStatus(true);
            ServiceItem service = new ServiceItem(); service.setServiceName("Cleaning Service"); service.setServiceType("SERVICE"); service.setUnitPrice(new BigDecimal("300000")); service.setUnit(unitService); service.setStatus(true);
            ServiceItem penalty = new ServiceItem(); penalty.setServiceName("Late Penalty"); penalty.setServiceType("PENALTY"); penalty.setUnitPrice(new BigDecimal("200000")); penalty.setUnit(unitService); penalty.setStatus(true);
            serviceItemRepository.saveAll(Arrays.asList(rent, electricity, water, parking, service, penalty));
        }

        // 4. Initialize Apartments
        if (apartmentRepository.count() == 0) {
            Apartment a1 = new Apartment(); a1.setApartmentNumber("A-101"); a1.setFloor((byte) 1); a1.setArea(new BigDecimal("75.00")); a1.setRoomType("2BR"); a1.setStatus((byte) 1); a1.setMaxOccupancy((byte) 4);
            Apartment a2 = new Apartment(); a2.setApartmentNumber("B-205"); a2.setFloor((byte) 2); a2.setArea(new BigDecimal("60.00")); a2.setRoomType("1BR"); a2.setStatus((byte) 1); a2.setMaxOccupancy((byte) 2);
            Apartment a3 = new Apartment(); a3.setApartmentNumber("C-312"); a3.setFloor((byte) 3); a3.setArea(new BigDecimal("90.00")); a3.setRoomType("3BR"); a3.setStatus((byte) 1); a3.setMaxOccupancy((byte) 6);
            Apartment a4 = new Apartment(); a4.setApartmentNumber("A-214"); a4.setFloor((byte) 2); a4.setArea(new BigDecimal("80.00")); a4.setRoomType("2BR"); a4.setStatus((byte) 1); a4.setMaxOccupancy((byte) 4);
            Apartment a5 = new Apartment(); a5.setApartmentNumber("D-108"); a5.setFloor((byte) 1); a5.setArea(new BigDecimal("70.00")); a5.setRoomType("2BR"); a5.setStatus((byte) 1); a5.setMaxOccupancy((byte) 4);
            Apartment a6 = new Apartment(); a6.setApartmentNumber("B-401"); a6.setFloor((byte) 4); a6.setArea(new BigDecimal("100.00")); a6.setRoomType("3BR"); a6.setStatus((byte) 1); a6.setMaxOccupancy((byte) 6);
            apartmentRepository.saveAll(Arrays.asList(a1, a2, a3, a4, a5, a6));
        }

        // 5. Link profiles to apartments via ResidentApartment
        if (residentApartmentRepository.count() == 0) {
            List<Account> accounts = accountRepository.findAll();
            List<Apartment> apartments = apartmentRepository.findAll();

            // Link existing accounts to first apartments
            for (int i = 0; i < accounts.size() && i < apartments.size(); i++) {
                Profile profile = accounts.get(i).getProfile();
                if (profile != null) {
                    ResidentApartment ra = new ResidentApartment();
                    ra.setProfile(profile);
                    ra.setApartment(apartments.get(i));
                    ra.setMoveInDate(LocalDate.of(2023, 1, 1));
                    residentApartmentRepository.save(ra);
                }
            }

            // Create additional residents for remaining apartments
            String[][] extraResidents = {
                {"A-214", "Tran Thi B"},
                {"D-108", "Le Van C"},
                {"B-401", "Pham Thi D"}
            };
            for (String[] res : extraResidents) {
                Apartment apt = apartmentRepository.findByApartmentNumber(res[0]).orElse(null);
                if (apt == null) continue;

                Account dummyAcc = new Account();
                dummyAcc.setUsername(res[1].toLowerCase().replace(" ", "."));
                dummyAcc.setPassword("password123");
                dummyAcc.setRole(roleRepository.findAll().stream().filter(r -> r.getRoleName().equals("Resident")).findFirst().orElse(null));
                dummyAcc = accountRepository.save(dummyAcc);

                Profile dummyProf = new Profile();
                dummyProf.setAccount(dummyAcc);
                dummyProf.setFullName(res[1]);
                profileRepository.save(dummyProf);

                ResidentApartment ra = new ResidentApartment();
                ra.setProfile(dummyProf);
                ra.setApartment(apt);
                ra.setMoveInDate(LocalDate.of(2023, 6, 1));
                residentApartmentRepository.save(ra);
            }
        }

        // 6. Initialize Bills
        if (billRepository.count() == 0) {
            List<Account> allAccounts = accountRepository.findAll();
            if (allAccounts.isEmpty()) return;
            Account admin = allAccounts.stream().filter(a -> "alex.mercer".equals(a.getUsername())).findFirst().orElse(allAccounts.get(0));
            Account manager = allAccounts.stream().filter(a -> "jane.doe".equals(a.getUsername())).findFirst().orElse(admin);
            List<Apartment> apartments = apartmentRepository.findAll();
            List<ServiceItem> services = serviceItemRepository.findAll();

            ServiceItem rentFee = services.stream().filter(s -> "RENT".equals(s.getServiceType())).findFirst().orElse(services.get(0));
            ServiceItem electricity = services.stream().filter(s -> "ELECTRICITY".equals(s.getServiceType())).findFirst().orElse(services.get(0));
            ServiceItem water = services.stream().filter(s -> "WATER".equals(s.getServiceType())).findFirst().orElse(services.get(0));
            ServiceItem parking = services.stream().filter(s -> "PARKING".equals(s.getServiceType())).findFirst().orElse(services.get(0));
            ServiceItem cleanService = services.stream().filter(s -> "SERVICE".equals(s.getServiceType())).findFirst().orElse(services.get(0));
            ServiceItem penalty = services.stream().filter(s -> "PENALTY".equals(s.getServiceType())).findFirst().orElse(services.get(0));

            Account[] creators = {admin, manager};
            Byte[] statuses = {1, 0, 2, 1, 1, 0, 2, 1, 0, 1};
            String[][] types = {
                {"RENT", "ELECTRICITY"}, {"RENT", "WATER"}, {"RENT", "ELECTRICITY", "WATER"},
                {"RENT"}, {"RENT", "PARKING"}, {"RENT", "ELECTRICITY"}, {"RENT", "WATER", "PARKING"},
                {"RENT", "ELECTRICITY", "WATER"}, {"RENT"}, {"RENT", "SERVICE"}
            };
            String[][] notes = {
                {"Monthly rent", "Electricity usage 320 kWh"}, {"Monthly rent", "Water usage 15m\u00B3"},
                {"Monthly rent", "Electricity 280 kWh", "Water usage 12m\u00B3"},
                {"Monthly rent"}, {"Monthly rent", "Parking slot P-22"},
                {"Monthly rent", "Electricity 350 kWh"},
                {"Monthly rent", "Water 18m\u00B3", "Parking P-15"},
                {"Monthly rent", "Electricity 300 kWh", "Water 14m\u00B3"},
                {"Monthly rent"}, {"Monthly rent", "Cleaning service"}
            };
            double[] amounts = {
                5850000, 5300000, 5600000, 5000000, 5500000,
                6050000, 5800000, 5600000, 5000000, 5300000
            };

            for (int i = 0; i < statuses.length && i < apartments.size(); i++) {
                Apartment apt = apartments.get(i);
                Byte status = statuses[i];
                Account creator = creators[i % 2];

                Bill bill = new Bill();
                bill.setApartment(apt);
                bill.setCreatedBy(creator);
                bill.setBillMonth((byte) 6);
                bill.setBillYear((short) 2026);
                bill.setTotalAmount(new BigDecimal(amounts[i]));
                bill.setStatus(status);
                bill.setDueDate(LocalDateTime.of(2026, 7, 10, 0, 0));
                bill.setCreatedDate(LocalDateTime.of(2026, 6, 1 + i, 8, 0));
                if (status == 1) {
                    bill.setPaidDate(LocalDateTime.of(2026, 6, 25 + (i % 5), 10, 30));
                }
                bill = billRepository.save(bill);

                // Create BillDetails
                String[] typeNames = types[i];
                String[] noteLines = notes[i];
                for (int j = 0; j < typeNames.length; j++) {
                    ServiceItem svc = switch (typeNames[j]) {
                        case "RENT" -> rentFee;
                        case "ELECTRICITY" -> electricity;
                        case "WATER" -> water;
                        case "PARKING" -> parking;
                        case "SERVICE" -> cleanService;
                        case "PENALTY" -> penalty;
                        default -> rentFee;
                    };
                    BigDecimal lineAmount = svc.getUnitPrice();
                    if ("ELECTRICITY".equals(typeNames[j])) lineAmount = new BigDecimal("1050000");
                    if ("WATER".equals(typeNames[j])) lineAmount = new BigDecimal("270000");
                    if ("PARKING".equals(typeNames[j])) lineAmount = new BigDecimal("500000");
                    if ("SERVICE".equals(typeNames[j])) lineAmount = new BigDecimal("300000");

                    BillDetail detail = new BillDetail();
                    detail.setBill(bill);
                    detail.setServiceItem(svc);
                    detail.setQuantity(BigDecimal.ONE);
                    detail.setAmount(lineAmount);
                    detail.setDescription(noteLines.length > j ? noteLines[j] : "");
                    billDetailRepository.save(detail);
                }
            }
        }

        // 7. Initialize SystemLogs
        if (systemLogRepository.count() == 0) {
            Account alex = accountRepository.findAll().stream().filter(a -> a.getUsername().equals("alex.mercer")).findFirst().orElse(null);
            Account jane = accountRepository.findAll().stream().filter(a -> a.getUsername().equals("jane.doe")).findFirst().orElse(null);
            Account ryan = accountRepository.findAll().stream().filter(a -> a.getUsername().equals("ryan.smith")).findFirst().orElse(null);

            SystemLog log1 = new SystemLog(); log1.setAccount(alex); log1.setAction("LOGIN"); log1.setEntityType("Account"); log1.setEntityId(alex != null ? alex.getAccountId() : 1); log1.setIpAddress("192.168.1.100"); log1.setCreatedAt(LocalDateTime.of(2026, 6, 28, 14, 22, 11)); systemLogRepository.save(log1);
            SystemLog log2 = new SystemLog(); log2.setAccount(jane); log2.setAction("CREATE_BILL"); log2.setEntityType("Bill"); log2.setEntityId(101); log2.setIpAddress("192.168.1.102"); log2.setCreatedAt(LocalDateTime.of(2026, 6, 28, 14, 15, 4)); systemLogRepository.save(log2);
            SystemLog log3 = new SystemLog(); log3.setAccount(null); log3.setAction("LOCK_ACCOUNT"); log3.setEntityType("Account"); log3.setEntityId(12); log3.setIpAddress("127.0.0.1"); log3.setCreatedAt(LocalDateTime.of(2026, 6, 28, 13, 58, 22)); systemLogRepository.save(log3);
            SystemLog log4 = new SystemLog(); log4.setAccount(alex); log4.setAction("CANCEL_BOOKING"); log4.setEntityType("Booking"); log4.setEntityId(205); log4.setIpAddress("192.168.1.100"); log4.setCreatedAt(LocalDateTime.of(2026, 6, 27, 12, 42, 10)); systemLogRepository.save(log4);
            SystemLog log5 = new SystemLog(); log5.setAccount(ryan); log5.setAction("UPDATE_PERMISSIONS"); log5.setEntityType("Account"); log5.setEntityId(3); log5.setIpAddress("192.168.1.105"); log5.setCreatedAt(LocalDateTime.of(2026, 6, 27, 12, 10, 55)); systemLogRepository.save(log5);

            for (int i = 1; i <= 30; i++) {
                SystemLog log = new SystemLog();
                if (i % 3 == 0) {
                    log.setAccount(alex); log.setAction("UPDATE_APARTMENT"); log.setEntityType("Apartment"); log.setEntityId(15); log.setIpAddress("192.168.1.100");
                } else if (i % 3 == 1) {
                    log.setAccount(jane); log.setAction("APPROVE_VEHICLE"); log.setEntityType("Vehicle"); log.setEntityId(50 + i); log.setIpAddress("192.168.1.102");
                } else {
                    log.setAccount(null); log.setAction("DAILY_BACKUP"); log.setEntityType("System"); log.setEntityId(0); log.setIpAddress("127.0.0.1");
                }
                log.setCreatedAt(LocalDateTime.of(2026, 6, 15, 8, 0, 0).plusHours(i * 7).plusMinutes(i * 13));
                systemLogRepository.save(log);
            }
        }

        // 8. Initialize Maintenance Requests
        if (requestRepo.count() == 0) {
            List<Apartment> apts = apartmentRepository.findAll();
            List<Profile> profiles = profileRepository.findAll();
            Account admin = accountRepository.findAll().stream().filter(a -> a.getUsername().equals("alex.mercer")).findFirst().orElse(null);
            if (apts.isEmpty() || profiles.isEmpty()) return;

            String[][] reqData = {
                {"Water leak in bathroom", "Water leaking from ceiling in master bathroom", "0", "0"},
                {"Electrical outlet not working", "Living room outlet stopped working after storm", "1", "1"},
                {"Broken window", "Living room window cracked, needs replacement", "0", "2"},
                {"AC not cooling", "Air conditioner not cooling properly, temperature stays at 28C", "2", "3"},
                {"Door handle loose", "Main door handle is loose and needs tightening", "0", "4"},
                {"Pest control needed", "Cockroaches spotted in kitchen, need fumigation", "3", "5"},
            };

            for (String[] row : reqData) {
                int idxApt = Integer.parseInt(row[3]);
                if (idxApt >= apts.size()) continue;
                Apartment apt = apts.get(idxApt);
                Profile pf = profiles.get(idxApt % profiles.size());

                MaintenanceRequest req = new MaintenanceRequest();
                req.setProfile(pf);
                req.setApartment(apt);
                req.setTitle(row[0]);
                req.setDescription(row[1]);
                req.setRequestDate(LocalDateTime.now().minusDays(idxApt * 2));
                req.setStatus(Byte.parseByte(row[2]));
                requestRepo.save(req);
            }
        }

        // 9. Initialize MaintenanceTasks + Reports
        if (taskRepo.count() == 0) {
            List<MaintenanceRequest> requests = requestRepo.findAll();
            List<Account> allAccounts = accountRepository.findAll();
            Account admin = allAccounts.stream().filter(a -> "alex.mercer".equals(a.getUsername())).findFirst().orElse(allAccounts.isEmpty() ? null : allAccounts.get(0));

            for (MaintenanceRequest req : requests) {
                if (req.getStatus() == 0 || req.getStatus() == 3) continue;

                MaintenanceTask task = new MaintenanceTask();
                task.setMaintenanceRequest(req);
                task.setStaff(allAccounts.get(req.getRequestId() % allAccounts.size()));
                task.setAssignedBy(admin);
                task.setAssignedDate(req.getRequestDate());
                task.setDeadline(req.getRequestDate().plusDays(7));
                task.setStatus(req.getStatus() == 2 ? (byte) 3 : (byte) 2);
                taskRepo.save(task);

                if (task.getStatus() == 3) {
                    MaintenanceReport rpt = new MaintenanceReport();
                    rpt.setMaintenanceTask(task);
                    rpt.setReportContent("Fixed the issue. All good now.");
                    rpt.setProgressPercent((byte) 100);
                    rpt.setCreatedAt(task.getAssignedDate().plusDays(3));
                    reportRepo.save(rpt);
                } else {
                    MaintenanceReport rpt = new MaintenanceReport();
                    rpt.setMaintenanceTask(task);
                    rpt.setReportContent("Initial inspection completed. Parts ordered.");
                    rpt.setProgressPercent((byte) 50);
                    rpt.setCreatedAt(task.getAssignedDate().plusDays(1));
                    reportRepo.save(rpt);
                }
            }
        }
    }
}

