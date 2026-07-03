package com.quan.apartment_building_management_system.dto.booking;

/**
 * Filter request DTO for Utility Booking list endpoint.
 * All fields are optional; null/blank values are ignored in query building.
 */
public class UtilityBookingFilterRequest {

    private String residentName;
    private String startTimeFrom;   // format: yyyy-MM-dd
    private String startTimeTo;     // format: yyyy-MM-dd
    private String createdAtFrom;   // format: yyyy-MM-dd
    private String createdAtTo;     // format: yyyy-MM-dd
    private Integer bookingStatus;     // null=All | 0=Pending | 1=Approved | 2=Rejected | 3=Cancelled
    private String paymentStatus;   // null=All | "paid" | "unpaid"
    private Integer utilityId;
    private int page = 0;
    private int size = 5;

    public UtilityBookingFilterRequest() {}

    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }

    public String getStartTimeFrom() { return startTimeFrom; }
    public void setStartTimeFrom(String startTimeFrom) { this.startTimeFrom = startTimeFrom; }

    public String getStartTimeTo() { return startTimeTo; }
    public void setStartTimeTo(String startTimeTo) { this.startTimeTo = startTimeTo; }

    public String getCreatedAtFrom() { return createdAtFrom; }
    public void setCreatedAtFrom(String createdAtFrom) { this.createdAtFrom = createdAtFrom; }

    public String getCreatedAtTo() { return createdAtTo; }
    public void setCreatedAtTo(String createdAtTo) { this.createdAtTo = createdAtTo; }

    public Integer getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(Integer bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Integer getUtilityId() { return utilityId; }
    public void setUtilityId(Integer utilityId) { this.utilityId = utilityId; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(0, page); }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
