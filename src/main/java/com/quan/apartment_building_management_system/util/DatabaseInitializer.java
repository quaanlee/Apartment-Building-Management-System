package com.quan.apartment_building_management_system.util;

import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final ApartmentRepository apartmentRepository;
    private final UnitRepository unitRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final UtilityRepository utilityRepository;
    private final UtilityPriceRepository utilityPriceRepository;
    private final UtilityResourceRepository utilityResourceRepository;
    private final UtilityBookingRepository utilityBookingRepository;
    private final ResidentApartmentRepository residentApartmentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final BillRepository billRepository;
    private final BillDetailRepository billDetailRepository;
    private final PaymentRepository paymentRepository;

    public DatabaseInitializer(RoleRepository roleRepository,
                               AccountRepository accountRepository,
                               ProfileRepository profileRepository,
                               ApartmentRepository apartmentRepository,
                               UnitRepository unitRepository,
                               ServiceItemRepository serviceItemRepository,
                               UtilityRepository utilityRepository,
                               UtilityPriceRepository utilityPriceRepository,
                               UtilityResourceRepository utilityResourceRepository,
                               UtilityBookingRepository utilityBookingRepository,
                               ResidentApartmentRepository residentApartmentRepository,
                               PaymentMethodRepository paymentMethodRepository,
                               BillRepository billRepository,
                               BillDetailRepository billDetailRepository,
                               PaymentRepository paymentRepository) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.apartmentRepository = apartmentRepository;
        this.unitRepository = unitRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.utilityRepository = utilityRepository;
        this.utilityPriceRepository = utilityPriceRepository;
        this.utilityResourceRepository = utilityResourceRepository;
        this.utilityBookingRepository = utilityBookingRepository;
        this.residentApartmentRepository = residentApartmentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeOrUpdateUtilities();

        if (roleRepository.count() > 0) {
            return; // Already initialized
        }

        // 1. Roles
        Role adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        adminRole = roleRepository.save(adminRole);

        Role managerRole = new Role();
        managerRole.setRoleName("MANAGER");
        managerRole = roleRepository.save(managerRole);

        Role residentRole = new Role();
        residentRole.setRoleName("RESIDENT");
        residentRole = roleRepository.save(residentRole);

        Role staffRole = new Role();
        staffRole.setRoleName("MAINTENANCE_STAFF");
        staffRole = roleRepository.save(staffRole);

        // 2. Accounts
        Account adminAcc = new Account();
        adminAcc.setUsername("admin");
        adminAcc.setPassword("admin");
        adminAcc.setRole(adminRole);
        adminAcc.setStatus(true);
        adminAcc = accountRepository.save(adminAcc);

        Account managerAcc = new Account();
        managerAcc.setUsername("manager");
        managerAcc.setPassword("manager");
        managerAcc.setRole(managerRole);
        managerAcc.setStatus(true);
        managerAcc = accountRepository.save(managerAcc);

        Account residentAcc1 = new Account();
        residentAcc1.setUsername("resident1");
        residentAcc1.setPassword("resident1");
        residentAcc1.setRole(residentRole);
        residentAcc1.setStatus(true);
        residentAcc1 = accountRepository.save(residentAcc1);

        Account residentAcc2 = new Account();
        residentAcc2.setUsername("resident2");
        residentAcc2.setPassword("resident2");
        residentAcc2.setRole(residentRole);
        residentAcc2.setStatus(true);
        residentAcc2 = accountRepository.save(residentAcc2);

        Account staffAcc = new Account();
        staffAcc.setUsername("staff");
        staffAcc.setPassword("staff");
        staffAcc.setRole(staffRole);
        staffAcc.setStatus(true);
        staffAcc = accountRepository.save(staffAcc);

        // 3. Profiles
        Profile adminProfile = new Profile();
        adminProfile.setAccount(adminAcc);
        adminProfile.setFullName("Lê Vũ Anh Quân");
        adminProfile.setEmail("quanlva@abms.com");
        adminProfile.setPhoneNumber("0987654321");
        adminProfile.setCitizenId("000000000001");
        adminProfile.setNationality("Vietnam");
        adminProfile.setEthnicity("Kinh");
        profileRepository.save(adminProfile);

        Profile managerProfile = new Profile();
        managerProfile.setAccount(managerAcc);
        managerProfile.setFullName("Nguyễn Đức Nam");
        managerProfile.setEmail("namnd@abms.com");
        managerProfile.setPhoneNumber("0912345678");
        managerProfile.setCitizenId("000000000002");
        managerProfile.setNationality("Vietnam");
        managerProfile.setEthnicity("Kinh");
        profileRepository.save(managerProfile);

        Profile residentProfile1 = new Profile();
        residentProfile1.setAccount(residentAcc1);
        residentProfile1.setFullName("Phan Công Sơn");
        residentProfile1.setEmail("sonpc@abms.com");
        residentProfile1.setPhoneNumber("0909090909");
        residentProfile1.setCitizenId("012345678901");
        residentProfile1.setNationality("Vietnam");
        residentProfile1.setEthnicity("Kinh");
        residentProfile1 = profileRepository.save(residentProfile1);

        Profile residentProfile2 = new Profile();
        residentProfile2.setAccount(residentAcc2);
        residentProfile2.setFullName("Trần Minh Thảo");
        residentProfile2.setEmail("thaotm@abms.com");
        residentProfile2.setPhoneNumber("0919191919");
        residentProfile2.setCitizenId("012345678902");
        residentProfile2.setNationality("Vietnam");
        residentProfile2.setEthnicity("Kinh");
        residentProfile2 = profileRepository.save(residentProfile2);

        Profile staffProfile = new Profile();
        staffProfile.setAccount(staffAcc);
        staffProfile.setFullName("Hà Mạnh Luân");
        staffProfile.setEmail("luanhm@abms.com");
        staffProfile.setPhoneNumber("0934567890");
        staffProfile.setCitizenId("000000000003");
        staffProfile.setNationality("Vietnam");
        staffProfile.setEthnicity("Kinh");
        profileRepository.save(staffProfile);

        // 4. Units (if empty)
        Unit hourUnit = unitRepository.findByUnitName("Hour").orElseGet(() -> {
            Unit u = new Unit();
            u.setUnitName("Hour");
            return unitRepository.save(u);
        });

        Unit dayUnit = unitRepository.findByUnitName("Day").orElseGet(() -> {
            Unit u = new Unit();
            u.setUnitName("Day");
            return unitRepository.save(u);
        });

        Unit monthUnit = unitRepository.findByUnitName("Month").orElseGet(() -> {
            Unit u = new Unit();
            u.setUnitName("Month");
            return unitRepository.save(u);
        });

        Unit turnUnit = unitRepository.findByUnitName("Turn").orElseGet(() -> {
            Unit u = new Unit();
            u.setUnitName("Turn");
            return unitRepository.save(u);
        });

        // 5. Apartments
        Apartment apt101 = new Apartment();
        apt101.setApartmentNumber("A-101");
        apt101.setFloor((byte) 1);
        apt101.setArea(new BigDecimal("75.50"));
        apt101.setRoomType("2BHK");
        apt101.setStatus((byte) 1); // Occupied
        apt101.setMaxOccupancy((byte) 4);
        apt101 = apartmentRepository.save(apt101);

        Apartment apt102 = new Apartment();
        apt102.setApartmentNumber("A-102");
        apt102.setFloor((byte) 1);
        apt102.setArea(new BigDecimal("90.00"));
        apt102.setRoomType("3BHK");
        apt102.setStatus((byte) 1); // Occupied
        apt102.setMaxOccupancy((byte) 6);
        apt102 = apartmentRepository.save(apt102);

        Apartment apt201 = new Apartment();
        apt201.setApartmentNumber("B-201");
        apt201.setFloor((byte) 2);
        apt201.setArea(new BigDecimal("75.50"));
        apt201.setRoomType("2BHK");
        apt201.setStatus((byte) 0); // Available
        apt201.setMaxOccupancy((byte) 4);
        apt201 = apartmentRepository.save(apt201);

        // Assign Residents to Apartments
        residentProfile1.setApartment(apt101);
        profileRepository.save(residentProfile1);

        residentProfile2.setApartment(apt102);
        profileRepository.save(residentProfile2);

        ResidentApartment ra1 = new ResidentApartment();
        ra1.setProfile(residentProfile1);
        ra1.setApartment(apt101);
        ra1.setMoveInDate(LocalDate.now().minusMonths(3));
        residentApartmentRepository.save(ra1);

        ResidentApartment ra2 = new ResidentApartment();
        ra2.setProfile(residentProfile2);
        ra2.setApartment(apt102);
        ra2.setMoveInDate(LocalDate.now().minusMonths(2));
        residentApartmentRepository.save(ra2);

        // 6. Services (ServiceItem)
        ServiceItem managementFee = new ServiceItem();
        managementFee.setServiceName("Management Fee");
        managementFee.setServiceType("Monthly Fee");
        managementFee.setUnitPrice(new BigDecimal("50.00"));
        managementFee.setUnit(monthUnit);
        managementFee.setStatus(true);
        serviceItemRepository.save(managementFee);

        ServiceItem electricity = new ServiceItem();
        electricity.setServiceName("Electricity");
        electricity.setServiceType("Utility Usage");
        electricity.setUnitPrice(new BigDecimal("0.15"));
        electricity.setUnit(hourUnit);
        electricity.setStatus(true);
        serviceItemRepository.save(electricity);

        ServiceItem water = new ServiceItem();
        water.setServiceName("Water");
        water.setServiceType("Utility Usage");
        water.setUnitPrice(new BigDecimal("2.50"));
        water.setUnit(turnUnit);
        water.setStatus(true);
        serviceItemRepository.save(water);

        ServiceItem parkingFee = new ServiceItem();
        parkingFee.setServiceName("Parking Fee");
        parkingFee.setServiceType("Monthly Fee");
        parkingFee.setUnitPrice(new BigDecimal("10.00"));
        parkingFee.setUnit(monthUnit);
        parkingFee.setStatus(true);
        serviceItemRepository.save(parkingFee);

        ServiceItem cleaningFee = new ServiceItem();
        cleaningFee.setServiceName("Cleaning Service");
        cleaningFee.setServiceType("Monthly Fee");
        cleaningFee.setUnitPrice(new BigDecimal("5.00"));
        cleaningFee.setUnit(monthUnit);
        cleaningFee.setStatus(true);
        serviceItemRepository.save(cleaningFee);

        ServiceItem utilityBookingServiceItem = new ServiceItem();
        utilityBookingServiceItem.setServiceName("Utility Booking");
        utilityBookingServiceItem.setServiceType("Utility Booking");
        utilityBookingServiceItem.setUnitPrice(BigDecimal.ZERO);
        utilityBookingServiceItem.setUnit(turnUnit);
        utilityBookingServiceItem.setStatus(true);
        serviceItemRepository.save(utilityBookingServiceItem);

        // 7. Payment Methods
        PaymentMethod cashMethod = new PaymentMethod();
        cashMethod.setMethodName("Cash");
        cashMethod.setIsOnline(false);
        cashMethod.setStatus(true);
        paymentMethodRepository.save(cashMethod);

        PaymentMethod vnPayMethod = new PaymentMethod();
        vnPayMethod.setMethodName("VNPAY");
        vnPayMethod.setIsOnline(true);
        vnPayMethod.setStatus(true);
        paymentMethodRepository.save(vnPayMethod);

        // 8. Utilities Configuration (if empty in UtilityPrice)
        Utility gym = utilityRepository.findById(1).orElse(null);
        Utility swimming = utilityRepository.findById(2).orElse(null);

        UtilityPrice gymPrice = null;

        // 9. Utility Resources
        UtilityResource gymResource = null;
        if (gym != null) {
            gymResource = new UtilityResource();
            gymResource.setUtility(gym);
            gymResource.setResourceName("Cardio & Strength Zone");
            gymResource.setLocation("Floor 2 - Block A");
            gymResource.setStatus(true);
            gymResource = utilityResourceRepository.save(gymResource);

            gymPrice = new UtilityPrice();
            gymPrice.setResource(gymResource);
            gymPrice.setUnit(hourUnit);
            gymPrice.setPrice(new BigDecimal("5.00"));
            gymPrice = utilityPriceRepository.save(gymPrice);
        }

        if (swimming != null) {
            UtilityResource swimmingResource = new UtilityResource();
            swimmingResource.setUtility(swimming);
            swimmingResource.setResourceName("Main Pool");
            swimmingResource.setLocation("Rooftop");
            swimmingResource.setStatus(true);
            swimmingResource = utilityResourceRepository.save(swimmingResource);

            UtilityPrice swimPrice = new UtilityPrice();
            swimPrice.setResource(swimmingResource);
            swimPrice.setUnit(turnUnit);
            swimPrice.setPrice(new BigDecimal("10.00"));
            utilityPriceRepository.save(swimPrice);
        }

        // 10. Sample Utility Booking (Approved booking for current month)
        if (residentProfile1 != null && gymResource != null && gymPrice != null) {
            UtilityBooking booking = new UtilityBooking();
            booking.setProfile(residentProfile1);
            booking.setResource(gymResource);
            booking.setUtilityPrice(gymPrice);
            booking.setStartTime(LocalDateTime.now().minusDays(5).withHour(10).withMinute(0));
            booking.setEndTime(LocalDateTime.now().minusDays(5).withHour(12).withMinute(0));
            booking.setTotalPrice(new BigDecimal("10.00")); // 2 hours
            booking.setStatus((byte) 1); // APPROVED
            booking.setApprovedBy(managerAcc);
            booking.setCreatedAt(LocalDateTime.now().minusDays(6));
            utilityBookingRepository.save(booking);
        }

        // 11. Sample Bills
        if (apt101 != null && managerAcc != null) {
            // Bill 1: Paid bill
            Bill bill1 = new Bill();
            bill1.setApartment(apt101);
            bill1.setCreatedBy(managerAcc);
            bill1.setBillMonth((byte) 5);
            bill1.setBillYear((short) 2026);
            bill1.setStatus((byte) 1); // PAID
            bill1.setDueDate(LocalDateTime.now().minusDays(10));
            bill1.setCreatedDate(LocalDateTime.now().minusDays(25));
            bill1.setPaidDate(LocalDateTime.now().minusDays(12));
            bill1.setTotalAmount(new BigDecimal("78.00"));
            bill1 = billRepository.save(bill1);

            BillDetail detail1 = new BillDetail();
            detail1.setBill(bill1);
            detail1.setServiceItem(managementFee);
            detail1.setQuantity(BigDecimal.ONE);
            detail1.setDescription("Management Fee");
            detail1.setAmount(new BigDecimal("50.00"));
            billDetailRepository.save(detail1);

            BillDetail detail2 = new BillDetail();
            detail2.setBill(bill1);
            detail2.setServiceItem(parkingFee);
            detail2.setQuantity(BigDecimal.ONE);
            detail2.setDescription("Parking Fee");
            detail2.setAmount(new BigDecimal("10.00"));
            billDetailRepository.save(detail2);

            BillDetail detail3 = new BillDetail();
            detail3.setBill(bill1);
            detail3.setServiceItem(electricity);
            detail3.setQuantity(new BigDecimal("120.00"));
            detail3.setDescription("Electricity usage: 120 kWh");
            detail3.setAmount(new BigDecimal("18.00"));
            billDetailRepository.save(detail3);

            Payment payment1 = new Payment();
            payment1.setBill(bill1);
            payment1.setPaidBy(residentProfile1.getAccount());
            payment1.setPaymentMethod(vnPayMethod);
            payment1.setAmount(new BigDecimal("78.00"));
            payment1.setPaymentDate(LocalDateTime.now().minusDays(12));
            payment1.setStatus((byte) 1); // SUCCESS
            payment1.setTransactionCode("TXN-VNPAY-998877");
            paymentRepository.save(payment1);
        }

        if (apt102 != null && managerAcc != null) {
            // Bill 2: Unpaid bill
            Bill bill2 = new Bill();
            bill2.setApartment(apt102);
            bill2.setCreatedBy(managerAcc);
            bill2.setBillMonth((byte) 6);
            bill2.setBillYear((short) 2026);
            bill2.setStatus((byte) 0); // UNPAID
            bill2.setDueDate(LocalDateTime.now().plusDays(10));
            bill2.setCreatedDate(LocalDateTime.now().minusDays(5));
            bill2.setTotalAmount(new BigDecimal("95.00"));
            bill2 = billRepository.save(bill2);

            BillDetail detail1 = new BillDetail();
            detail1.setBill(bill2);
            detail1.setServiceItem(managementFee);
            detail1.setQuantity(BigDecimal.ONE);
            detail1.setDescription("Management Fee");
            detail1.setAmount(new BigDecimal("50.00"));
            billDetailRepository.save(detail1);

            BillDetail detail2 = new BillDetail();
            detail2.setBill(bill2);
            detail2.setServiceItem(parkingFee);
            detail2.setQuantity(BigDecimal.ONE);
            detail2.setDescription("Parking Fee");
            detail2.setAmount(new BigDecimal("10.00"));
            billDetailRepository.save(detail2);

            BillDetail detail3 = new BillDetail();
            detail3.setBill(bill2);
            detail3.setServiceItem(water);
            detail3.setQuantity(new BigDecimal("14.00"));
            detail3.setDescription("Water usage: 14 m3");
            detail3.setAmount(new BigDecimal("35.00"));
            billDetailRepository.save(detail3);
        }

        if (apt101 != null && managerAcc != null) {
            // Bill 3: Overdue bill
            Bill bill3 = new Bill();
            bill3.setApartment(apt101);
            bill3.setCreatedBy(managerAcc);
            bill3.setBillMonth((byte) 4);
            bill3.setBillYear((short) 2026);
            bill3.setStatus((byte) 2); // OVERDUE
            bill3.setDueDate(LocalDateTime.now().minusDays(30));
            bill3.setCreatedDate(LocalDateTime.now().minusDays(45));
            bill3.setTotalAmount(new BigDecimal("60.00"));
            bill3 = billRepository.save(bill3);

            BillDetail detail1 = new BillDetail();
            detail1.setBill(bill3);
            detail1.setServiceItem(managementFee);
            detail1.setQuantity(BigDecimal.ONE);
            detail1.setDescription("Management Fee");
            detail1.setAmount(new BigDecimal("50.00"));
            billDetailRepository.save(detail1);

            BillDetail detail2 = new BillDetail();
            detail2.setBill(bill3);
            detail2.setServiceItem(parkingFee);
            detail2.setQuantity(BigDecimal.ONE);
            detail2.setDescription("Parking Fee");
            detail2.setAmount(new BigDecimal("10.00"));
            billDetailRepository.save(detail2);
        }
    }

    private void initializeOrUpdateUtilities() {
        // Fetch units
        Unit hourUnit = unitRepository.findByUnitName("Hour").orElse(null);
        Unit turnUnit = unitRepository.findByUnitName("Turn").orElse(null);

        // 1. Gym / Modern Fitness Center
        Utility gym = utilityRepository.findByUtilityName("Gym")
                .orElseGet(() -> utilityRepository.findByUtilityName("Modern Fitness Center").orElse(null));
        if (gym != null) {
            gym.setUtilityName("Modern Fitness Center");
            gym.setDescription("Phòng tập Gym hiện đại với đầy đủ trang thiết bị fitness, cardio và tạ.");
            utilityRepository.save(gym);
        } else {
            gym = new Utility();
            gym.setUtilityName("Modern Fitness Center");
            gym.setDescription("Phòng tập Gym hiện đại với đầy đủ trang thiết bị fitness, cardio và tạ.");
            gym.setStatus(true);
            gym = utilityRepository.save(gym);
        }

        // 2. Swimming / Rooftop Infinity Pool
        Utility swimming = utilityRepository.findByUtilityName("Swimming")
                .orElseGet(() -> utilityRepository.findByUtilityName("Rooftop Infinity Pool").orElse(null));
        if (swimming != null) {
            swimming.setUtilityName("Rooftop Infinity Pool");
            swimming.setDescription("Bể bơi vô cực trên tầng thượng tòa nhà, có khu vực cho trẻ em và người lớn.");
            utilityRepository.save(swimming);
        } else {
            swimming = new Utility();
            swimming.setUtilityName("Rooftop Infinity Pool");
            swimming.setDescription("Bể bơi vô cực trên tầng thượng tòa nhà, có khu vực cho trẻ em và người lớn.");
            swimming.setStatus(true);
            swimming = utilityRepository.save(swimming);
        }

        // 3. Club Lounge
        Utility lounge = utilityRepository.findByUtilityName("Club Lounge").orElse(null);
        if (lounge != null) {
            lounge.setDescription("Phòng sinh hoạt chung cao cấp dành cho cư dân hội họp hoặc tiếp khách.");
            utilityRepository.save(lounge);
        } else {
            lounge = new Utility();
            lounge.setUtilityName("Club Lounge");
            lounge.setDescription("Phòng sinh hoạt chung cao cấp dành cho cư dân hội họp hoặc tiếp khách.");
            lounge.setStatus(true);
            lounge = utilityRepository.save(lounge);
        }

        // 4. Private Cinema Room
        Utility cinema = utilityRepository.findByUtilityName("Private Cinema Room").orElse(null);
        if (cinema == null) {
            cinema = new Utility();
            cinema.setUtilityName("Private Cinema Room");
            cinema.setDescription("Phòng chiếu phim gia đình riêng tư với hệ thống âm thanh vòm đỉnh cao.");
            cinema.setStatus(false); // MAINTENANCE
            cinema = utilityRepository.save(cinema);
        }
        // Seed Price for Cinema
        UtilityResource cinemaResourceForPrice = cinema != null ? utilityResourceRepository.findByUtilityUtilityId(cinema.getUtilityId()).stream().findFirst().orElse(null) : null;
        if (cinemaResourceForPrice != null && hourUnit != null && utilityPriceRepository.findByResourceResourceIdAndUnitUnitId(cinemaResourceForPrice.getResourceId(), hourUnit.getUnitId()).isEmpty()) {
            UtilityPrice price = new UtilityPrice();
            price.setResource(cinemaResourceForPrice);
            price.setUnit(hourUnit);
            price.setPrice(new BigDecimal("12.00"));
            utilityPriceRepository.save(price);
        }
        // Seed Resource for Cinema
        if (cinema != null && utilityResourceRepository.findByUtilityUtilityId(cinema.getUtilityId()).isEmpty()) {
            UtilityResource res = new UtilityResource();
            res.setUtility(cinema);
            res.setResourceName("Premium Theater Screen");
            res.setLocation("Tầng 1 - Block B");
            res.setStatus(false); // Maintenance
            utilityResourceRepository.save(res);
        }

        // 5. Sky Garden & BBQ Area
        Utility garden = utilityRepository.findByUtilityName("Sky Garden & BBQ Area").orElse(null);
        if (garden == null) {
            garden = new Utility();
            garden.setUtilityName("Sky Garden & BBQ Area");
            garden.setDescription("Khu vườn trên mây thoáng mát kết hợp khu vực bếp nướng BBQ gia đình.");
            garden.setStatus(true);
            garden = utilityRepository.save(garden);
        }
        // Seed Price for Garden
        UtilityResource gardenResourceForPrice = garden != null ? utilityResourceRepository.findByUtilityUtilityId(garden.getUtilityId()).stream().findFirst().orElse(null) : null;
        if (gardenResourceForPrice != null && turnUnit != null && utilityPriceRepository.findByResourceResourceIdAndUnitUnitId(gardenResourceForPrice.getResourceId(), turnUnit.getUnitId()).isEmpty()) {
            UtilityPrice price = new UtilityPrice();
            price.setResource(gardenResourceForPrice);
            price.setUnit(turnUnit);
            price.setPrice(new BigDecimal("20.00"));
            utilityPriceRepository.save(price);
        }
        // Seed Resource for Garden
        if (garden != null && utilityResourceRepository.findByUtilityUtilityId(garden.getUtilityId()).isEmpty()) {
            UtilityResource res1 = new UtilityResource();
            res1.setUtility(garden);
            res1.setResourceName("BBQ Grill Pavilion A");
            res1.setLocation("Tầng thượng - Block A");
            res1.setStatus(true);
            utilityResourceRepository.save(res1);

            UtilityResource res2 = new UtilityResource();
            res2.setUtility(garden);
            res2.setResourceName("BBQ Grill Pavilion B");
            res2.setLocation("Tầng thượng - Block A");
            res2.setStatus(true);
            utilityResourceRepository.save(res2);
        }
    }
}
