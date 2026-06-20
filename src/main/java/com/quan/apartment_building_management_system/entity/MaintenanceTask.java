package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MaintenanceTask")
public class MaintenanceTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TaskID")
    private Integer taskId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RequestID", nullable = false, unique = true)
    private MaintenanceRequest maintenanceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StaffID", nullable = false)
    private Account staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignedBy", nullable = false)
    private Account assignedBy;

    @Column(name = "AssignedDate", nullable = false)
    private LocalDateTime assignedDate = LocalDateTime.now();

    @Column(name = "Deadline")
    private LocalDateTime deadline;

    @Column(name = "Status", nullable = false)
    private Byte status = 1;

    @OneToMany(mappedBy = "maintenanceTask")
    private List<MaintenanceReport> maintenanceReports = new ArrayList<>();

    public MaintenanceTask() {
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public MaintenanceRequest getMaintenanceRequest() {
        return maintenanceRequest;
    }

    public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
        this.maintenanceRequest = maintenanceRequest;
    }

    public Account getStaff() {
        return staff;
    }

    public void setStaff(Account staff) {
        this.staff = staff;
    }

    public Account getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Account assignedBy) {
        this.assignedBy = assignedBy;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public List<MaintenanceReport> getMaintenanceReports() {
        return maintenanceReports;
    }

    public void setMaintenanceReports(List<MaintenanceReport> maintenanceReports) {
        this.maintenanceReports = maintenanceReports;
    }
}
