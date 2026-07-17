package com.quan.apartment_building_management_system.dataInitializer;

import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final ApartmentRepository apartmentRepository;
    private final ApartmentImageRepository apartmentImageRepository;

    private final ResidentApartmentRepository residentApartmentRepository;
    private final VehicleRepository vehicleRepository;
    private final UnitRepository unitRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final UtilityRepository utilityRepository;
    private final UtilityResourceRepository utilityResourceRepository;
    private final UtilityPriceRepository utilityPriceRepository;
    private final UtilityImageRepository utilityImageRepository;
    private final UtilityBookingRepository utilityBookingRepository;
    private final BillRepository billRepository;
    private final BillDetailRepository billDetailRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRepository paymentRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final MaintenanceTaskRepository maintenanceTaskRepository;
    private final MaintenanceReportRepository maintenanceReportRepository;
    private final MaintenanceReportImageRepository maintenanceReportImageRepository;
    private final MaintenanceRequestImageRepository maintenanceRequestImageRepository;
    private final NotificationRepository notificationRepository;
    private final AccountNotificationRepository accountNotificationRepository;
    private final SystemLogRepository systemLogRepository;


    public DataInitializer(
            RoleRepository roleRepository,
            AccountRepository accountRepository,
            ProfileRepository profileRepository,
            ApartmentRepository apartmentRepository,
            ApartmentImageRepository apartmentImageRepository,

            ResidentApartmentRepository residentApartmentRepository,
            VehicleRepository vehicleRepository,
            UnitRepository unitRepository,
            ServiceItemRepository serviceItemRepository,
            UtilityRepository utilityRepository,
            UtilityResourceRepository utilityResourceRepository,
            UtilityPriceRepository utilityPriceRepository,
            UtilityImageRepository utilityImageRepository,
            UtilityBookingRepository utilityBookingRepository,
            BillRepository billRepository,
            BillDetailRepository billDetailRepository,
            PaymentMethodRepository paymentMethodRepository,
            PaymentRepository paymentRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            MaintenanceTaskRepository maintenanceTaskRepository,
            MaintenanceReportRepository maintenanceReportRepository,
            MaintenanceReportImageRepository maintenanceReportImageRepository,
            MaintenanceRequestImageRepository maintenanceRequestImageRepository,
            NotificationRepository notificationRepository,
            AccountNotificationRepository accountNotificationRepository,
            SystemLogRepository systemLogRepository
    ) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.apartmentRepository = apartmentRepository;
        this.apartmentImageRepository = apartmentImageRepository;

        this.residentApartmentRepository = residentApartmentRepository;
        this.vehicleRepository = vehicleRepository;
        this.unitRepository = unitRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.utilityRepository = utilityRepository;
        this.utilityResourceRepository = utilityResourceRepository;
        this.utilityPriceRepository = utilityPriceRepository;
        this.utilityImageRepository = utilityImageRepository;
        this.utilityBookingRepository = utilityBookingRepository;
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentRepository = paymentRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.maintenanceTaskRepository = maintenanceTaskRepository;
        this.maintenanceReportRepository = maintenanceReportRepository;
        this.maintenanceReportImageRepository = maintenanceReportImageRepository;
        this.maintenanceRequestImageRepository = maintenanceRequestImageRepository;
        this.notificationRepository = notificationRepository;
        this.accountNotificationRepository = accountNotificationRepository;
        this.systemLogRepository = systemLogRepository;

    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Roles (Admin, Manager, Maintenance Staff, Resident)
        Role adminRole = null;
        Role managerRole = null;
        Role staffRole = null;
        Role residentRole = null;

        if (roleRepository.count() == 0) {
            adminRole = new Role();
            adminRole.setRoleName("Admin");
            adminRole = roleRepository.save(adminRole);

            managerRole = new Role();
            managerRole.setRoleName("Manager");
            managerRole = roleRepository.save(managerRole);

            residentRole = new Role();
            residentRole.setRoleName("Resident");
            residentRole = roleRepository.save(residentRole);

            staffRole = new Role();
            staffRole.setRoleName("Maintenance Staff");
            staffRole = roleRepository.save(staffRole);
        } else {
            adminRole = roleRepository.findAll().stream().filter(r -> "Admin".equalsIgnoreCase(r.getRoleName())).findFirst().orElse(null);
            managerRole = roleRepository.findAll().stream().filter(r -> "Manager".equalsIgnoreCase(r.getRoleName())).findFirst().orElse(null);
            staffRole = roleRepository.findAll().stream().filter(r -> "Maintenance Staff".equalsIgnoreCase(r.getRoleName()) || "Maintenance_Staff".equalsIgnoreCase(r.getRoleName())).findFirst().orElse(null);
            residentRole = roleRepository.findAll().stream().filter(r -> "Resident".equalsIgnoreCase(r.getRoleName())).findFirst().orElse(null);
        }

        // 2. Units (kWh, m³, m², Giờ, Ngày, Tháng, Lượt, Lần)
        Unit kwhUnit = null;
        Unit m3Unit = null;
        Unit m2Unit = null;
        Unit gioUnit = null;
        Unit ngayUnit = null;
        Unit thangUnit = null;
        Unit luotUnit = null;
        Unit lanUnit = null;

        if (unitRepository.count() == 0) {
            String[] unitNames = {"kWh", "m³", "m²", "Giờ", "Ngày", "Tháng", "Lượt", "Lần"};
            for (String name : unitNames) {
                Unit u = new Unit();
                u.setUnitName(name);
                unitRepository.save(u);
            }
        }
        
        List<Unit> allUnits = unitRepository.findAll();
        kwhUnit = allUnits.stream().filter(u -> "kWh".equals(u.getUnitName())).findFirst().orElse(null);
        m3Unit = allUnits.stream().filter(u -> "m³".equals(u.getUnitName())).findFirst().orElse(null);
        m2Unit = allUnits.stream().filter(u -> "m²".equals(u.getUnitName())).findFirst().orElse(null);
        gioUnit = allUnits.stream().filter(u -> "Giờ".equals(u.getUnitName())).findFirst().orElse(null);
        ngayUnit = allUnits.stream().filter(u -> "Ngày".equals(u.getUnitName())).findFirst().orElse(null);
        thangUnit = allUnits.stream().filter(u -> "Tháng".equals(u.getUnitName())).findFirst().orElse(null);
        luotUnit = allUnits.stream().filter(u -> "Lượt".equals(u.getUnitName())).findFirst().orElse(null);
        lanUnit = allUnits.stream().filter(u -> "Lần".equals(u.getUnitName())).findFirst().orElse(null);

        // 3. Accounts (Username as email format, Password "123456")
        Account adminAcc = null;
        Account managerAcc = null;
        Account staffAcc = null;
        Account residentAcc = null;

        if (accountRepository.count() == 0) {
            adminAcc = new Account();
            adminAcc.setUsername("admin@gmail.com");
            adminAcc.setPassword("123456");
            adminAcc.setRole(adminRole);
            adminAcc.setStatus(true);
            adminAcc = accountRepository.save(adminAcc);

            managerAcc = new Account();
            managerAcc.setUsername("manager@gmail.com");
            managerAcc.setPassword("123456");
            managerAcc.setRole(managerRole);
            managerAcc.setStatus(true);
            managerAcc = accountRepository.save(managerAcc);

            residentAcc = new Account();
            residentAcc.setUsername("resident@gmail.com");
            residentAcc.setPassword("123456");
            residentAcc.setRole(residentRole);
            residentAcc.setStatus(true);
            residentAcc = accountRepository.save(residentAcc);

            staffAcc = new Account();
            staffAcc.setUsername("staff@gmail.com");
            staffAcc.setPassword("123456");
            staffAcc.setRole(staffRole);
            staffAcc.setStatus(true);
            staffAcc = accountRepository.save(staffAcc);
        } else {
            adminAcc = accountRepository.findByUsername("admin@gmail.com").orElse(null);
            managerAcc = accountRepository.findByUsername("manager@gmail.com").orElse(null);
            staffAcc = accountRepository.findByUsername("staff@gmail.com").orElse(null);
            residentAcc = accountRepository.findByUsername("resident@gmail.com").orElse(null);
        }

        // 4. Apartments
        Apartment apt101 = null;
        Apartment apt102 = null;
        Apartment apt201 = null;

        if (apartmentRepository.count() == 0) {
            apt101 = new Apartment();
            apt101.setApartmentNumber("A-101");
            apt101.setFloor((byte) 1);
            apt101.setArea(new BigDecimal("75.50"));
            apt101.setRoomType("2BHK");
            apt101.setStatus((byte) 1); // Occupied
            apt101.setMaxOccupancy((byte) 4);
            apt101 = apartmentRepository.save(apt101);

            apt102 = new Apartment();
            apt102.setApartmentNumber("A-102");
            apt102.setFloor((byte) 1);
            apt102.setArea(new BigDecimal("90.00"));
            apt102.setRoomType("3BHK");
            apt102.setStatus((byte) 1); // Occupied
            apt102.setMaxOccupancy((byte) 6);
            apt102 = apartmentRepository.save(apt102);

            apt201 = new Apartment();
            apt201.setApartmentNumber("B-201");
            apt201.setFloor((byte) 2);
            apt201.setArea(new BigDecimal("60.00"));
            apt201.setRoomType("1BHK");
            apt201.setStatus((byte) 0); // Available
            apt201.setMaxOccupancy((byte) 2);
            apt201 = apartmentRepository.save(apt201);
        } else {
            List<Apartment> apts = apartmentRepository.findAll();
            if (!apts.isEmpty()) {
                apt101 = apts.get(0);
                if (apts.size() > 1) apt102 = apts.get(1);
                if (apts.size() > 2) apt201 = apts.get(2);
            }
        }

        // 5. Profiles (VN phone numbers starting with 09...)
        Profile adminProfile = null;
        Profile managerProfile = null;
        Profile staffProfile = null;
        Profile residentProfile = null;

        if (profileRepository.count() == 0) {
            if (adminAcc != null) {
                adminProfile = new Profile();
                adminProfile.setAccount(adminAcc);
                adminProfile.setFullName("Lê Anh Quân");
                adminProfile.setGender("Nam");
                adminProfile.setDateOfBirth(LocalDate.of(1990, 5, 20));
                adminProfile.setCitizenId("012345678901");
                adminProfile.setNationality("Vietnam");
                adminProfile.setEthnicity("Kinh");
                adminProfile.setPhoneNumber("0987654321");
                adminProfile.setEmail("admin@gmail.com");
                adminProfile = profileRepository.save(adminProfile);
            }

            if (managerAcc != null) {
                managerProfile = new Profile();
                managerProfile.setAccount(managerAcc);
                managerProfile.setFullName("Trần Văn Quản Lý");
                managerProfile.setGender("Nam");
                managerProfile.setDateOfBirth(LocalDate.of(1985, 10, 15));
                managerProfile.setCitizenId("012345678902");
                managerProfile.setNationality("Vietnam");
                managerProfile.setEthnicity("Kinh");
                managerProfile.setPhoneNumber("0912345678");
                managerProfile.setEmail("manager@gmail.com");
                managerProfile = profileRepository.save(managerProfile);
            }

            if (residentAcc != null) {
                residentProfile = new Profile();
                residentProfile.setAccount(residentAcc);
                residentProfile.setApartment(apt101);
                residentProfile.setFullName("Phạm Thị Cư Dân");
                residentProfile.setGender("Nữ");
                residentProfile.setDateOfBirth(LocalDate.of(1995, 7, 7));
                residentProfile.setCitizenId("012345678904");
                residentProfile.setNationality("Vietnam");
                residentProfile.setEthnicity("Kinh");
                residentProfile.setPhoneNumber("0934567890");
                residentProfile.setEmail("resident@gmail.com");
                residentProfile.setRelationshipToOwner("Chủ hộ");
                residentProfile.setIsHouseholdOwner(true);
                residentProfile.setMoveInDate(LocalDate.now().minusMonths(6));
                residentProfile = profileRepository.save(residentProfile);
            }

            if (staffAcc != null) {
                staffProfile = new Profile();
                staffProfile.setAccount(staffAcc);
                staffProfile.setFullName("Nguyễn Văn Bảo Trì");
                staffProfile.setGender("Nam");
                staffProfile.setDateOfBirth(LocalDate.of(1993, 2, 28));
                staffProfile.setCitizenId("012345678903");
                staffProfile.setNationality("Vietnam");
                staffProfile.setEthnicity("Kinh");
                staffProfile.setPhoneNumber("0923456789");
                staffProfile.setEmail("staff@gmail.com");
                staffProfile = profileRepository.save(staffProfile);
            }
        } else {
            List<Profile> profs = profileRepository.findAll();
            for (Profile p : profs) {
                if (p.getAccount() != null) {
                    if ("admin@gmail.com".equals(p.getAccount().getUsername())) adminProfile = p;
                    else if ("manager@gmail.com".equals(p.getAccount().getUsername())) managerProfile = p;
                    else if ("staff@gmail.com".equals(p.getAccount().getUsername())) staffProfile = p;
                    else if ("resident@gmail.com".equals(p.getAccount().getUsername())) residentProfile = p;
                }
            }
        }

        // 6. ApartmentImage
        if (apartmentImageRepository.count() == 0) {
            if (apt101 != null) {
                ApartmentImage img = new ApartmentImage();
                img.setApartment(apt101);
                img.setImageUrl("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267");
                img.setImageTitle("Phòng khách A-101");
                img.setPrimary(true);
                img.setUploadedAt(LocalDateTime.now());
                apartmentImageRepository.save(img);
            }
            if (apt102 != null) {
                ApartmentImage img = new ApartmentImage();
                img.setApartment(apt102);
                img.setImageUrl("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688");
                img.setImageTitle("Phòng ngủ A-102");
                img.setPrimary(true);
                img.setUploadedAt(LocalDateTime.now());
                apartmentImageRepository.save(img);
            }
        }


        // 8. ResidentApartment
        if (residentApartmentRepository.count() == 0) {
            if (residentProfile != null && apt101 != null) {
                ResidentApartment ra = new ResidentApartment();
                ra.setProfile(residentProfile);
                ra.setApartment(apt101);
                ra.setMoveInDate(LocalDate.now().minusMonths(6));
                residentApartmentRepository.save(ra);
            }
        }


        // 10. Vehicle
        if (vehicleRepository.count() == 0) {
            if (residentProfile != null) {
                Vehicle v = new Vehicle();
                v.setProfile(residentProfile);
                v.setLicensePlate("29A-88888");
                v.setVehicleType("Xe máy");
                v.setBrand("Honda SH");
                v.setColor("Đỏ đen");
                v.setRegisteredDate(LocalDate.now().minusMonths(4));
                v.setStatus((byte) 1); // APPROVED
                v.setApprovedBy(managerAcc);
                v.setApprovedAt(LocalDateTime.now().minusMonths(4));
                vehicleRepository.save(v);
            }
        }

        // 11. ServiceItem (Dịch vụ - VND Pricing)
        ServiceItem dien = null;
        ServiceItem nuoc = null;
        ServiceItem xe = null;
        ServiceItem vesinh = null;

        if (serviceItemRepository.count() == 0) {
            dien = new ServiceItem();
            dien.setServiceName("Điện sinh hoạt");
            dien.setServiceType("Điện");
            dien.setUnitPrice(new BigDecimal("3500")); // 3.500 VND/kWh
            dien.setUnit(kwhUnit);
            dien.setStatus(true);
            dien.setDescription("Tiền điện tính theo chỉ số công tơ");
            dien = serviceItemRepository.save(dien);

            nuoc = new ServiceItem();
            nuoc.setServiceName("Nước sinh hoạt");
            nuoc.setServiceType("Nước");
            nuoc.setUnitPrice(new BigDecimal("15000")); // 15.000 VND/m³
            nuoc.setUnit(m3Unit);
            nuoc.setStatus(true);
            nuoc.setDescription("Tiền nước tính theo m³ tiêu dùng");
            nuoc = serviceItemRepository.save(nuoc);

            xe = new ServiceItem();
            xe.setServiceName("Gửi xe máy");
            xe.setServiceType("Gửi xe");
            xe.setUnitPrice(new BigDecimal("120000")); // 120.000 VND/Tháng
            xe.setUnit(thangUnit);
            xe.setStatus(true);
            xe.setDescription("Phí gửi xe máy cố định hàng tháng");
            xe = serviceItemRepository.save(xe);

            vesinh = new ServiceItem();
            vesinh.setServiceName("Phí dịch vụ chung cư");
            vesinh.setServiceType("Vệ sinh");
            vesinh.setUnitPrice(new BigDecimal("10000")); // 10.000 VND/m²
            vesinh.setUnit(m2Unit);
            vesinh.setStatus(true);
            vesinh.setDescription("Phí quản lý, vệ sinh, bảo vệ chung cư");
            vesinh = serviceItemRepository.save(vesinh);
        } else {
            List<ServiceItem> services = serviceItemRepository.findAll();
            dien = services.stream().filter(s -> "Điện sinh hoạt".equals(s.getServiceName())).findFirst().orElse(null);
            nuoc = services.stream().filter(s -> "Nước sinh hoạt".equals(s.getServiceName())).findFirst().orElse(null);
            xe = services.stream().filter(s -> "Gửi xe máy".equals(s.getServiceName())).findFirst().orElse(null);
            vesinh = services.stream().filter(s -> "Phí dịch vụ chung cư".equals(s.getServiceName())).findFirst().orElse(null);
        }

        // 12. Utility
        Utility pool = null;
        Utility bbq = null;

        if (utilityRepository.count() == 0) {
            pool = new Utility();
            pool.setUtilityName("Bể bơi tầng mây");
            pool.setDescription("Bể bơi vô cực ngoài trời tầng thượng");
            pool.setImageUrl("https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7");
            pool.setStatus(true);
            pool.setType(true); // Reservable
            pool = utilityRepository.save(pool);

            bbq = new Utility();
            bbq.setUtilityName("Khu nướng BBQ");
            bbq.setDescription("Khu vực lò nướng BBQ ngoài trời tại công viên");
            bbq.setImageUrl("https://images.unsplash.com/photo-1555939594-58d7cb561ad1");
            bbq.setStatus(true);
            bbq.setType(true); // Reservable
            bbq = utilityRepository.save(bbq);
        } else {
            List<Utility> utils = utilityRepository.findAll();
            pool = utils.stream().filter(u -> u.getUtilityName().contains("Bể bơi")).findFirst().orElse(null);
            bbq = utils.stream().filter(u -> u.getUtilityName().contains("BBQ")).findFirst().orElse(null);
        }

        // 13. UtilityResource
        UtilityResource poolLane1 = null;
        UtilityResource bbqTable1 = null;

        if (utilityResourceRepository.count() == 0) {
            if (pool != null) {
                poolLane1 = new UtilityResource();
                poolLane1.setUtility(pool);
                poolLane1.setResourceName("Làn bơi số 1");
                poolLane1.setLocation("Tầng 25 - Block A");
                poolLane1.setDescription("Làn bơi VIP có view thành phố");
                poolLane1.setStatus(true);
                poolLane1 = utilityResourceRepository.save(poolLane1);
            }

            if (bbq != null) {
                bbqTable1 = new UtilityResource();
                bbqTable1.setUtility(bbq);
                bbqTable1.setResourceName("Bàn tiệc BBQ #1");
                bbqTable1.setLocation("Công viên trung tâm");
                bbqTable1.setDescription("Bàn ăn ngoài trời kèm bếp nướng sẵn có");
                bbqTable1.setStatus(true);
                bbqTable1 = utilityResourceRepository.save(bbqTable1);
            }
        } else {
            List<UtilityResource> resources = utilityResourceRepository.findAll();
            poolLane1 = resources.stream().filter(r -> r.getResourceName().contains("Làn bơi")).findFirst().orElse(null);
            bbqTable1 = resources.stream().filter(r -> r.getResourceName().contains("Bàn tiệc")).findFirst().orElse(null);
        }

        // 14. UtilityPrice (VND pricing)
        UtilityPrice poolPrice = null;
        UtilityPrice bbqPrice = null;

        if (utilityPriceRepository.count() == 0) {
            if (poolLane1 != null) {
                poolPrice = new UtilityPrice();
                poolPrice.setResource(poolLane1);
                poolPrice.setUnit(gioUnit);
                poolPrice.setPrice(new BigDecimal("50000")); // 50.000 VND / Giờ
                poolPrice = utilityPriceRepository.save(poolPrice);
            }

            if (bbqTable1 != null) {
                bbqPrice = new UtilityPrice();
                bbqPrice.setResource(bbqTable1);
                bbqPrice.setUnit(luotUnit);
                bbqPrice.setPrice(new BigDecimal("200000")); // 200.000 VND / Lượt
                bbqPrice = utilityPriceRepository.save(bbqPrice);
            }
        } else {
            List<UtilityPrice> prices = utilityPriceRepository.findAll();
            if (!prices.isEmpty()) {
                poolPrice = prices.get(0);
                if (prices.size() > 1) bbqPrice = prices.get(1);
            }
        }

        // 15. UtilityImage
        if (utilityImageRepository.count() == 0) {
            if (poolLane1 != null) {
                UtilityImage img = new UtilityImage();
                img.setResource(poolLane1);
                img.setImageUrl("https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7");
                img.setCaption("Hình ảnh làn bơi view thành phố");
                img.setPrimary(true);
                img.setCreatedDate(LocalDateTime.now());
                utilityImageRepository.save(img);
            }
        }

        // 16. UtilityBooking (VND total price)
        UtilityBooking booking = null;

        if (utilityBookingRepository.count() == 0) {
            if (residentProfile != null && poolLane1 != null && poolPrice != null) {
                booking = new UtilityBooking();
                booking.setProfile(residentProfile);
                booking.setResource(poolLane1);
                booking.setUtilityPrice(poolPrice);
                booking.setStartTime(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0));
                booking.setEndTime(LocalDateTime.now().plusDays(1).withHour(16).withMinute(0));
                booking.setTotalPrice(new BigDecimal("100000")); // 2 Giờ * 50.000 = 100.000 VND
                booking.setStatus((byte) 1); // APPROVED
                booking.setApprovedBy(managerAcc);
                booking.setCreatedAt(LocalDateTime.now());
                booking.setPaymentStatus(true);
                booking = utilityBookingRepository.save(booking);
            }
        } else {
            List<UtilityBooking> bookings = utilityBookingRepository.findAll();
            if (!bookings.isEmpty()) {
                booking = bookings.get(0);
            }
        }

        // 17. Bill (VND pricing)
        Bill bill = null;

        if (billRepository.count() == 0) {
            if (apt101 != null && adminAcc != null) {
                bill = new Bill();
                bill.setApartment(apt101);
                bill.setCreatedBy(adminAcc);
                bill.setBillMonth((byte) 7);
                bill.setBillYear((short) 2026);
                bill.setTotalAmount(new BigDecimal("485000")); // 485.000 VND
                bill.setStatus((byte) 1); // PAID
                bill.setDueDate(LocalDateTime.now().plusDays(10));
                bill.setCreatedDate(LocalDateTime.now());
                bill.setPaidDate(LocalDateTime.now().minusDays(1));
                bill = billRepository.save(bill);
            }
        } else {
            List<Bill> bills = billRepository.findAll();
            if (!bills.isEmpty()) {
                bill = bills.get(0);
            }
        }

        // 18. BillDetail
        if (billDetailRepository.count() == 0) {
            if (bill != null) {
                // Tiền điện
                BillDetail detailDien = new BillDetail();
                detailDien.setBill(bill);
                detailDien.setServiceItem(dien);
                detailDien.setQuantity(new BigDecimal("100")); // 100 kWh
                detailDien.setAmount(new BigDecimal("350000")); // 350.000 VND
                detailDien.setDescription("Chỉ số điện tháng 7: 100 kWh");
                billDetailRepository.save(detailDien);

                // Tiền nước
                BillDetail detailNuoc = new BillDetail();
                detailNuoc.setBill(bill);
                detailNuoc.setServiceItem(nuoc);
                detailNuoc.setQuantity(new BigDecimal("9")); // 9 m³
                detailNuoc.setAmount(new BigDecimal("135000")); // 135.000 VND
                detailNuoc.setDescription("Chỉ số nước tháng 7: 9 m³");
                billDetailRepository.save(detailNuoc);
            }
        }

        // 19. PaymentMethod
        PaymentMethod cash = null;
        PaymentMethod bank = null;

        if (paymentMethodRepository.count() == 0) {
            cash = new PaymentMethod();
            cash.setMethodName("Tiền mặt");
            cash.setIsOnline(false);
            cash.setStatus(true);
            cash = paymentMethodRepository.save(cash);

            bank = new PaymentMethod();
            bank.setMethodName("Chuyển khoản Ngân hàng / PayOS");
            bank.setIsOnline(true);
            bank.setStatus(true);
            bank = paymentMethodRepository.save(bank);
        } else {
            List<PaymentMethod> methods = paymentMethodRepository.findAll();
            cash = methods.stream().filter(m -> "Tiền mặt".equals(m.getMethodName())).findFirst().orElse(null);
            bank = methods.stream().filter(m -> m.getMethodName().contains("Chuyển khoản")).findFirst().orElse(null);
        }

        // 20. Payment
        if (paymentRepository.count() == 0) {
            if (bill != null && residentAcc != null && bank != null) {
                Payment p = new Payment();
                p.setBill(bill);
                p.setPaidBy(residentAcc);
                p.setPaymentMethod(bank);
                p.setTransactionCode("TXN123456789");
                p.setAmount(new BigDecimal("485000")); // 485.000 VND
                p.setPaymentDate(LocalDateTime.now().minusDays(1));
                p.setStatus((byte) 1); // SUCCESS
                paymentRepository.save(p);
            }
        }

        // 21. MaintenanceRequest
        MaintenanceRequest request = null;

        if (maintenanceRequestRepository.count() == 0) {
            if (residentProfile != null && apt101 != null) {
                request = new MaintenanceRequest();
                request.setProfile(residentProfile);
                request.setApartment(apt101);
                request.setTitle("Hỏng vòi nước nhà vệ sinh");
                request.setDescription("Vòi sen trong phòng tắm chính bị rò rỉ nước liên tục rất lãng phí");
                request.setRequestDate(LocalDateTime.now().minusDays(2));
                request.setStatus((byte) 1); // ASSIGNED
                request = maintenanceRequestRepository.save(request);
            }
        } else {
            List<MaintenanceRequest> requests = maintenanceRequestRepository.findAll();
            if (!requests.isEmpty()) {
                request = requests.get(0);
            }
        }

        // 22. MaintenanceTask
        MaintenanceTask task = null;

        if (maintenanceTaskRepository.count() == 0) {
            if (request != null && staffAcc != null && managerAcc != null) {
                task = new MaintenanceTask();
                task.setMaintenanceRequest(request);
                task.setStaff(staffAcc);
                task.setAssignedBy(managerAcc);
                task.setAssignedDate(LocalDateTime.now().minusDays(1));
                task.setDeadline(LocalDateTime.now().plusDays(1));
                task.setStatus((byte) 2); // IN_PROGRESS
                task = maintenanceTaskRepository.save(task);
            }
        } else {
            List<MaintenanceTask> tasks = maintenanceTaskRepository.findAll();
            if (!tasks.isEmpty()) {
                task = tasks.get(0);
            }
        }

        // 23. MaintenanceReport
        MaintenanceReport report = null;

        if (maintenanceReportRepository.count() == 0) {
            if (task != null) {
                report = new MaintenanceReport();
                report.setMaintenanceTask(task);
                report.setReportContent("Đã mua vòi nước mới và chuẩn bị lắp ráp thay thế");
                report.setProgressPercent((byte) 50);
                report.setCreatedAt(LocalDateTime.now());
                report = maintenanceReportRepository.save(report);
            }
        } else {
            List<MaintenanceReport> reports = maintenanceReportRepository.findAll();
            if (!reports.isEmpty()) {
                report = reports.get(0);
            }
        }

        // 24. MaintenanceReportImage
        if (maintenanceReportImageRepository.count() == 0) {
            if (report != null) {
                MaintenanceReportImage img = new MaintenanceReportImage();
                img.setReport(report);
                img.setImageUrl("https://images.unsplash.com/photo-1584622650111-993a426fbf0a");
                img.setCaption("Ảnh thiết bị vòi sen mới");
                maintenanceReportImageRepository.save(img);
            }
        }

        // 25. MaintenanceRequestImage
        if (maintenanceRequestImageRepository.count() == 0) {
            if (request != null) {
                MaintenanceRequestImage img = new MaintenanceRequestImage();
                img.setRequest(request);
                img.setImageUrl("https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1");
                img.setDescription("Ảnh vòi sen cũ bị rò nước");
                maintenanceRequestImageRepository.save(img);
            }
        }

        // 26. Notification
        Notification notification = null;

        if (notificationRepository.count() == 0) {
            if (adminAcc != null) {
                notification = new Notification();
                notification.setTitle("Thông báo họp cư dân định kỳ");
                notification.setContent("Kính gửi quý cư dân, ban quản lý trân trọng kính mời cư dân tham gia cuộc họp định kỳ vào Chủ Nhật tuần này.");
                notification.setNotificationType((byte) 3); // Tin chung
                notification.setCreatedBy(adminAcc);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setRecipient("TOÀN BỘ CƯ DÂN");
                notification = notificationRepository.save(notification);
            }
        } else {
            List<Notification> notifs = notificationRepository.findAll();
            if (!notifs.isEmpty()) {
                notification = notifs.get(0);
            }
        }

        // 27. AccountNotification
        if (accountNotificationRepository.count() == 0) {
            if (notification != null && residentAcc != null) {
                AccountNotification an = new AccountNotification();
                an.setNotification(notification);
                an.setAccount(residentAcc);
                an.setIsRead(false);
                accountNotificationRepository.save(an);
            }
        }

        // 28. SystemLog
        if (systemLogRepository.count() == 0) {
            if (adminAcc != null) {
                SystemLog log = new SystemLog();
                log.setAccount(adminAcc);
                log.setAction("SEED_DATA");
                log.setEntityType("System");
                log.setEntityId(1);
                log.setOldValue("{}");
                log.setNewValue("{}");
                log.setDescription("Khởi tạo hệ thống ban đầu và nạp dữ liệu mẫu");
                log.setCreatedAt(LocalDateTime.now());
                systemLogRepository.save(log);
            }
        }
    }
}
