package com.quan.apartment_building_management_system.dto.admin;

import com.quan.apartment_building_management_system.entity.SystemLog;
import java.util.List;

public class AdminDashboardStatsDto {
    private long totalAccounts;
    private long activeAccounts;
    
    private long totalApartments;
    private long occupiedApartments;
    
    private long totalUtilities;
    private long totalServices;
    
    private List<SystemLog> recentLogs;

    // Getters and Setters
    public long getTotalAccounts() { return totalAccounts; }
    public void setTotalAccounts(long totalAccounts) { this.totalAccounts = totalAccounts; }

    public long getActiveAccounts() { return activeAccounts; }
    public void setActiveAccounts(long activeAccounts) { this.activeAccounts = activeAccounts; }

    public long getTotalApartments() { return totalApartments; }
    public void setTotalApartments(long totalApartments) { this.totalApartments = totalApartments; }

    public long getOccupiedApartments() { return occupiedApartments; }
    public void setOccupiedApartments(long occupiedApartments) { this.occupiedApartments = occupiedApartments; }

    public long getTotalUtilities() { return totalUtilities; }
    public void setTotalUtilities(long totalUtilities) { this.totalUtilities = totalUtilities; }

    public long getTotalServices() { return totalServices; }
    public void setTotalServices(long totalServices) { this.totalServices = totalServices; }

    public List<SystemLog> getRecentLogs() { return recentLogs; }
    public void setRecentLogs(List<SystemLog> recentLogs) { this.recentLogs = recentLogs; }
}
