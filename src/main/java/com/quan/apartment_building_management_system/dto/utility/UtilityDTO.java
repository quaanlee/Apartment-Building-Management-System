package com.quan.apartment_building_management_system.dto.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UtilityDTO {
    private Integer utilityId;
    private String utilityName;
    private String description;
    private Boolean status;
    private List<Resource> utilityResources = new ArrayList<>();
    private List<Price> utilityPrices = new ArrayList<>();

    public UtilityDTO() {
    }

    public UtilityDTO(Integer utilityId, String utilityName, String description, Boolean status) {
        this.utilityId = utilityId;
        this.utilityName = utilityName;
        this.description = description;
        this.status = status;
    }

    public Integer getUtilityId() {
        return utilityId;
    }

    public void setUtilityId(Integer utilityId) {
        this.utilityId = utilityId;
    }

    public String getUtilityName() {
        return utilityName;
    }

    public void setUtilityName(String utilityName) {
        this.utilityName = utilityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Resource> getUtilityResources() {
        return utilityResources;
    }

    public void setUtilityResources(List<Resource> utilityResources) {
        this.utilityResources = utilityResources;
    }

    public List<Price> getUtilityPrices() {
        return utilityPrices;
    }

    public void setUtilityPrices(List<Price> utilityPrices) {
        this.utilityPrices = utilityPrices;
    }

    // --- Static Nested DTOs ---

    public static class Resource {
        private Integer resourceId;
        private Integer utilityId;
        private String utilityName;
        private String resourceName;
        private String location;
        private Boolean status;

        public Resource() {
        }

        public Resource(Integer resourceId, Integer utilityId, String utilityName, String resourceName, String location, Boolean status) {
            this.resourceId = resourceId;
            this.utilityId = utilityId;
            this.utilityName = utilityName;
            this.resourceName = resourceName;
            this.location = location;
            this.status = status;
        }

        public Integer getResourceId() {
            return resourceId;
        }

        public void setResourceId(Integer resourceId) {
            this.resourceId = resourceId;
        }

        public Integer getUtilityId() {
            return utilityId;
        }

        public void setUtilityId(Integer utilityId) {
            this.utilityId = utilityId;
        }

        public String getUtilityName() {
            return utilityName;
        }

        public void setUtilityName(String utilityName) {
            this.utilityName = utilityName;
        }

        public String getResourceName() {
            return resourceName;
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }
    }

    public static class Price {
        private Integer utilityPriceId;
        private UtilityDTO utility;
        private Unit unit;
        private BigDecimal price;

        public Price() {
        }

        public Price(Integer utilityPriceId, UtilityDTO utility, Unit unit, BigDecimal price) {
            this.utilityPriceId = utilityPriceId;
            this.utility = utility;
            this.unit = unit;
            this.price = price;
        }

        public Integer getUtilityPriceId() {
            return utilityPriceId;
        }

        public void setUtilityPriceId(Integer utilityPriceId) {
            this.utilityPriceId = utilityPriceId;
        }

        public UtilityDTO getUtility() {
            return utility;
        }

        public void setUtility(UtilityDTO utility) {
            this.utility = utility;
        }

        public Unit getUnit() {
            return unit;
        }

        public void setUnit(Unit unit) {
            this.unit = unit;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    public String getImageUrl() {
        if (utilityName == null) return "/images/utilities/default.jpg";
        String name = utilityName.toLowerCase();
        if (name.contains("swimming") || name.contains("pool")) return "/images/utilities/pool.jpg";
        if (name.contains("gym") || name.contains("fitness")) return "/images/utilities/gym.jpg";
        if (name.contains("lounge") || name.contains("club")) return "/images/utilities/lounge.jpg";
        if (name.contains("cinema")) return "/images/utilities/cinema.jpg";
        if (name.contains("garden") || name.contains("bbq")) return "/images/utilities/garden.jpg";
        return "/images/utilities/default.jpg";
    }

    public String getLocation() {
        if (utilityName == null) return "Floor 1";
        String name = utilityName.toLowerCase();
        if (name.contains("swimming") || name.contains("pool")) return "Floor 25, North Wing";
        if (name.contains("gym") || name.contains("fitness")) return "Floor 2, Block A";
        if (name.contains("lounge") || name.contains("club")) return "Floor G, Main Lobby";
        if (name.contains("cinema")) return "Floor 1, West Wing";
        if (name.contains("garden") || name.contains("bbq")) return "Rooftop, Block B";
        return "Floor 1";
    }

    public int getBookingsPerMonth() {
        if (utilityName == null) return 0;
        String name = utilityName.toLowerCase();
        if (name.contains("swimming") || name.contains("pool")) return 142;
        if (name.contains("gym") || name.contains("fitness")) return 285;
        if (name.contains("lounge") || name.contains("club")) return 98;
        if (name.contains("cinema")) return 45;
        if (name.contains("garden") || name.contains("bbq")) return 98;
        return 12;
    }

    public String getOperatingHoursWeekdays() {
        if (utilityName == null) return "08:00 AM - 10:00 PM";
        String name = utilityName.toLowerCase();
        if (name.contains("swimming") || name.contains("pool")) return "06:00 AM - 10:00 PM";
        if (name.contains("gym") || name.contains("fitness")) return "05:00 AM - 11:00 PM";
        if (name.contains("lounge") || name.contains("club")) return "08:00 AM - 09:00 PM";
        if (name.contains("cinema")) return "09:00 AM - 11:00 PM";
        if (name.contains("garden") || name.contains("bbq")) return "07:00 AM - 10:00 PM";
        return "08:00 AM - 10:00 PM";
    }

    public String getOperatingHoursWeekends() {
        if (utilityName == null) return "08:00 AM - 10:00 PM";
        String name = utilityName.toLowerCase();
        if (name.contains("swimming") || name.contains("pool")) return "08:00 AM - 11:00 PM";
        if (name.contains("gym") || name.contains("fitness")) return "06:00 AM - 10:00 PM";
        if (name.contains("lounge") || name.contains("club")) return "09:00 AM - 10:00 PM";
        if (name.contains("cinema")) return "09:00 AM - 11:59 PM";
        if (name.contains("garden") || name.contains("bbq")) return "07:00 AM - 11:00 PM";
        return "08:00 AM - 10:00 PM";
    }

    public String getNextMaintenance() {
        if (utilityName == null) return "None scheduled";
        String name = utilityName.toLowerCase();
        if (name.contains("swimming") || name.contains("pool")) return "Next maintenance: June 24th, 02:00 PM";
        if (name.contains("gym") || name.contains("fitness")) return "Next maintenance: First Monday of month";
        return "None scheduled";
    }

    public List<String> getFeatures() {
        List<String> list = new ArrayList<>();
        if (utilityName == null) return list;
        String name = utilityName.toLowerCase();
        if (name.contains("swimming") || name.contains("pool")) {
            list.add("Temperature controlled (28°C)");
            list.add("Certified lifeguard on duty");
            list.add("Premium towel service");
            list.add("Salt-water filtration");
        } else if (name.contains("gym") || name.contains("fitness")) {
            list.add("Premium cardio machines");
            list.add("Certified trainers on duty");
            list.add("Yoga & Dance studio included");
            list.add("Shower and sauna facilities");
        } else if (name.contains("lounge") || name.contains("club")) {
            list.add("High-speed WiFi");
            list.add("Free coffee & tea");
            list.add("Private meeting rooms");
            list.add("Printing & scanner access");
        } else if (name.contains("cinema")) {
            list.add("Dolby Atmos sound");
            list.add("4K Laser projection");
            list.add("Luxury recliner seats");
            list.add("Popcorn bar access");
        } else if (name.contains("garden") || name.contains("bbq")) {
            list.add("Premium BBQ grills");
            list.add("Cozy outdoor lounges");
            list.add("Dining pavilions");
            list.add("Panoramic city views");
        }
        return list;
    }

    public List<MembershipPlan> getMembershipPlans() {
        List<MembershipPlan> plans = new ArrayList<>();
        if (utilityName == null) return plans;
        String name = utilityName.toLowerCase();

        BigDecimal basePrice = null;
        String baseUnitName = "day";
        if (utilityPrices != null && !utilityPrices.isEmpty()) {
            Price p = utilityPrices.get(0);
            basePrice = p.getPrice();
            if (p.getUnit() != null && p.getUnit().getUnitName() != null) {
                baseUnitName = p.getUnit().getUnitName().toLowerCase();
            }
        }

        if (name.contains("swimming") || name.contains("pool")) {
            String basePriceStr = basePrice != null ? "$" + basePrice.intValue() : "$15";
            plans.add(new MembershipPlan("One-time Entry", basePriceStr, "/ " + baseUnitName, List.of("Full access for 1 day", "Towel service included")));
            plans.add(new MembershipPlan("Monthly Pass", "$120", "/ month", List.of("Unlimited access for 30 days", "Guest pass (1 per month)", "Locker access")));
            plans.add(new MembershipPlan("Annual Membership", "$1,200", "/ year", List.of("Best value - 365 days access", "Priority booking for events", "2 months free equivalent")));
        } else if (name.contains("gym") || name.contains("fitness")) {
            String basePriceStr = basePrice != null ? "$" + basePrice.intValue() : "$5";
            plans.add(new MembershipPlan("One-time Entry", basePriceStr, "/ " + baseUnitName, List.of("Hourly pass", "Full gym floor access")));
            plans.add(new MembershipPlan("Monthly Pass", "$50", "/ month", List.of("Unlimited access for 30 days", "1 complimentary trainer session")));
            plans.add(new MembershipPlan("Annual Membership", "$500", "/ year", List.of("Save 20%", "Lockers and towel service included")));
        } else if (name.contains("cinema")) {
            String basePriceStr = basePrice != null ? "$" + basePrice.intValue() : "$12";
            plans.add(new MembershipPlan("Hourly Booking", basePriceStr, "/ " + baseUnitName, List.of("Private theater screen access", "Dolby Atmos sound system")));
            plans.add(new MembershipPlan("Half-day Pass", "$40", "/ 4 hours", List.of("4 hours block reservation", "Complimentary popcorn bucket")));
            plans.add(new MembershipPlan("Full-day Pass", "$75", "/ day", List.of("8 hours block reservation", "All-day soft drinks and snacks")));
        } else if (name.contains("garden") || name.contains("bbq")) {
            String basePriceStr = basePrice != null ? "$" + basePrice.intValue() : "$20";
            plans.add(new MembershipPlan("Standard Booking", basePriceStr, "/ " + baseUnitName, List.of("3 hours pavilion reservation", "Premium BBQ grills access")));
            plans.add(new MembershipPlan("Monthly Pass", "$150", "/ month", List.of("Unlimited bookings", "Priority weekend booking slots")));
            plans.add(new MembershipPlan("Annual Pass", "$1,200", "/ year", List.of("Best value - unlimited year-round", "VIP party planning support")));
        } else {
            String basePriceStr = basePrice != null ? "$" + basePrice.intValue() : "$10";
            plans.add(new MembershipPlan("One-time Entry", basePriceStr, "/ " + baseUnitName, List.of("Full access for 1 day")));
            plans.add(new MembershipPlan("Monthly Pass", "$80", "/ month", List.of("Unlimited access for 30 days", "Locker access")));
            plans.add(new MembershipPlan("Annual Membership", "$800", "/ year", List.of("Save 20%", "Priority bookings")));
        }
        return plans;
    }

    public static class MembershipPlan {
        private String planName;
        private String price;
        private String unit;
        private List<String> benefits;

        public MembershipPlan(String planName, String price, String unit, List<String> benefits) {
            this.planName = planName;
            this.price = price;
            this.unit = unit;
            this.benefits = benefits;
        }

        public String getPlanName() { return planName; }
        public String getPrice() { return price; }
        public String getUnit() { return unit; }
        public List<String> getBenefits() { return benefits; }
    }

    public static class Unit {
        private Integer unitId;
        private String unitName;

        public Unit() {
        }

        public Unit(Integer unitId, String unitName) {
            this.unitId = unitId;
            this.unitName = unitName;
        }

        public Integer getUnitId() {
            return unitId;
        }

        public void setUnitId(Integer unitId) {
            this.unitId = unitId;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }
    }
}
