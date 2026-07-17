package com.quan.apartment_building_management_system.dto.systemlog;

public class AccountLogDTO {
    private Integer accountId;
    private String username;
    private String role;
    private Boolean status;
    private Byte failedLoginCount;
    private String lockedUntil;

    public AccountLogDTO() {}

    public AccountLogDTO(Integer accountId, String username, String role, Boolean status, Byte failedLoginCount, String lockedUntil) {
        this.accountId = accountId;
        this.username = username;
        this.role = role;
        this.status = status;
        this.failedLoginCount = failedLoginCount;
        this.lockedUntil = lockedUntil;
    }

    public static AccountLogDTO fromEntity(com.quan.apartment_building_management_system.entity.Account account) {
        if (account == null) return null;
        return new AccountLogDTO(
            account.getAccountId(),
            account.getUsername(),
            account.getRole() != null ? account.getRole().getRoleName() : null,
            account.getStatus(),
            account.getFailedLoginCount(),
            account.getLockedUntil() != null ? account.getLockedUntil().toString() : null
        );
    }

    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public Byte getFailedLoginCount() { return failedLoginCount; }
    public void setFailedLoginCount(Byte failedLoginCount) { this.failedLoginCount = failedLoginCount; }
    public String getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(String lockedUntil) { this.lockedUntil = lockedUntil; }
}
