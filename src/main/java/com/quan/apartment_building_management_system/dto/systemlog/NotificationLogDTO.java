package com.quan.apartment_building_management_system.dto.systemlog;

public class NotificationLogDTO {
    private Long notificationId;
    private String title;
    private Byte notificationType;
    private String relatedEntityType;
    private String recipient;

    public NotificationLogDTO() {}

    public NotificationLogDTO(Long notificationId, String title, Byte notificationType, String relatedEntityType, String recipient) {
        this.notificationId = notificationId;
        this.title = title;
        this.notificationType = notificationType;
        this.relatedEntityType = relatedEntityType;
        this.recipient = recipient;
    }

    public static NotificationLogDTO fromEntity(com.quan.apartment_building_management_system.entity.Notification notification) {
        if (notification == null) return null;
        return new NotificationLogDTO(
            notification.getNotificationId(),
            notification.getTitle(),
            notification.getNotificationType(),
            notification.getRelatedEntityType(),
            notification.getRecipient()
        );
    }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Byte getNotificationType() { return notificationType; }
    public void setNotificationType(Byte notificationType) { this.notificationType = notificationType; }
    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
}
