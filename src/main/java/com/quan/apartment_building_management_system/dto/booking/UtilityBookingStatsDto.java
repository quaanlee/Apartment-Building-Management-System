package com.quan.apartment_building_management_system.dto.booking;

/**
 * Stats DTO for the 3 summary cards at the top of the Utility Booking Management page.
 */
public class UtilityBookingStatsDto {

    private long totalBookings;
    private long pendingApprovals;
    private long todaySchedule;

    public UtilityBookingStatsDto() {}

    public UtilityBookingStatsDto(long totalBookings, long pendingApprovals, long todaySchedule) {
        this.totalBookings = totalBookings;
        this.pendingApprovals = pendingApprovals;
        this.todaySchedule = todaySchedule;
    }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getPendingApprovals() { return pendingApprovals; }
    public void setPendingApprovals(long pendingApprovals) { this.pendingApprovals = pendingApprovals; }

    public long getTodaySchedule() { return todaySchedule; }
    public void setTodaySchedule(long todaySchedule) { this.todaySchedule = todaySchedule; }
}
