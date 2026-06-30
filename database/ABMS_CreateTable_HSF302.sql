-- ============================================================
-- DATABASE: HSF302 - Hệ thống Quản lý Chung cư
-- Updated: 06/2026
-- ============================================================

CREATE DATABASE ABMS;
GO
USE ABMS;
GO

-- ============================================================
-- 1. Role
-- ============================================================
CREATE TABLE Role (
                      RoleID      INT             IDENTITY(1,1)   PRIMARY KEY,
                      RoleName    NVARCHAR(50)    NOT NULL        UNIQUE
    -- ADMIN | MANAGER | RESIDENT | MAINTENANCE_STAFF
);
GO

-- ============================================================
-- 2. Account
-- ============================================================
CREATE TABLE Account (
                         AccountID          INT          IDENTITY(1,1)  PRIMARY KEY,
                         Username           NVARCHAR(50) NOT NULL        UNIQUE,
                         Password           NVARCHAR(255)NOT NULL,
                         RoleID             INT          NOT NULL,
                         Status             BIT          NOT NULL        DEFAULT 1,  -- 1: Hoạt động | 0: Đã khóa
                         FailedLoginCount   TINYINT      NOT NULL        DEFAULT 0,  -- Số lần sai PW liên tiếp
                         LockedUntil        DATETIME     NULL,                       -- NULL = không bị khóa tạm
                         CreatedAt          DATETIME     NOT NULL        DEFAULT GETDATE(),

                         CONSTRAINT FK_Account_Role FOREIGN KEY (RoleID) REFERENCES Role(RoleID)
);
GO

-- ============================================================
-- 3. Apartment (Căn hộ)
-- ============================================================
CREATE TABLE Apartment (
                           ApartmentID     INT             IDENTITY(1,1)   PRIMARY KEY,
                           ApartmentNumber VARCHAR(20)     NOT NULL         UNIQUE,
                           Floor           TINYINT         NOT NULL,
                           Area            DECIMAL(8,2)    NOT NULL,
                           RoomType        NVARCHAR(50)    NOT NULL,
                           Status          TINYINT         NOT NULL         DEFAULT 0,  -- 0: Available | 1: Occupied | 2: Maintenance
                           MaxOccupancy    TINYINT         NOT NULL         DEFAULT 4   -- Số người tối đa
);
GO

-- ============================================================
-- 4. Profile (Hồ sơ cư dân)
-- ============================================================
CREATE TABLE Profile (
                         ProfileID                   INT             IDENTITY(1,1)   PRIMARY KEY,
                         AccountID                   INT             NULL,
                         ApartmentID                 INT             NULL,
                         FullName                    NVARCHAR(100)   NOT NULL,
                         Gender                      NVARCHAR(10)    NULL,
                         DateOfBirth                 DATE            NULL,
                         PlaceOfBirth                NVARCHAR(100)   NULL,
                         CitizenID                   VARCHAR(20)     NULL            UNIQUE,
                         CitizenIDIssueDate          DATE            NULL,
                         CitizenIDIssuePlace         NVARCHAR(100)   NULL,
                         Nationality                 NVARCHAR(50)    NULL,
                         Ethnicity                   NVARCHAR(50)    NULL,
                         Occupation                  NVARCHAR(100)   NULL,
                         PhoneNumber                 VARCHAR(15)     NULL,
                         Email                       NVARCHAR(100)   NULL,
                         AvatarURL                   NVARCHAR(500)   NULL,
                         EmergencyContactName        NVARCHAR(100)   NULL,
                         EmergencyContactPhone       VARCHAR(15)     NULL,
                         RelationshipToOwner         NVARCHAR(50)    NULL,           -- Chủ hộ, vợ, con,...
                         MoveInDate                  DATE            NULL,
                         MoveOutDate                 DATE            NULL,
                         IsHouseholdOwner            BIT             NOT NULL        DEFAULT 0,
                         ResidentStatus              TINYINT         NOT NULL        DEFAULT 1,  -- 1: Đang sống | 2: Đã chuyển ra | 3: Tạm trú
                         CreatedAt                   DATETIME        NOT NULL        DEFAULT GETDATE(),

                         CONSTRAINT FK_Profile_Account   FOREIGN KEY (AccountID)   REFERENCES Account(AccountID),
                         CONSTRAINT FK_Profile_Apartment FOREIGN KEY (ApartmentID) REFERENCES Apartment(ApartmentID)
);
GO

-- ============================================================
-- 5. ResidentApartment (Gán cư dân vào căn hộ)
-- ============================================================
CREATE TABLE ResidentApartment (
                                   ResidentApartmentID INT  IDENTITY(1,1)   PRIMARY KEY,
                                   ProfileID           INT  NOT NULL,
                                   ApartmentID         INT  NOT NULL,
                                   MoveInDate          DATE NOT NULL,
                                   MoveOutDate         DATE NULL,

                                   CONSTRAINT FK_ResidentApartment_Profile   FOREIGN KEY (ProfileID)   REFERENCES Profile(ProfileID),
                                   CONSTRAINT FK_ResidentApartment_Apartment FOREIGN KEY (ApartmentID) REFERENCES Apartment(ApartmentID)
);
GO

-- ============================================================
-- 6. Vehicle (Phương tiện)
-- ============================================================
CREATE TABLE Vehicle (
                         VehicleID       INT             IDENTITY(1,1)   PRIMARY KEY,
                         ProfileID       INT             NOT NULL,
                         LicensePlate    VARCHAR(20)     NOT NULL         UNIQUE,
                         VehicleType     NVARCHAR(50)    NOT NULL,
                         Brand           NVARCHAR(50)    NULL,
                         Color           NVARCHAR(30)    NULL,
                         RegisteredDate  DATE            NOT NULL         DEFAULT GETDATE(),
                         Status          TINYINT         NOT NULL         DEFAULT 0,  -- 0: PENDING | 1: APPROVED | 2: REJECTED | 3: INACTIVE
                         DocumentURL     NVARCHAR(500)   NULL,                        -- Ảnh giấy tờ xe
                         ApprovedBy      INT             NULL,                        -- Manager duyệt
                         ApprovedAt      DATETIME        NULL,

                         CONSTRAINT FK_Vehicle_Profile   FOREIGN KEY (ProfileID)  REFERENCES Profile(ProfileID),
                         CONSTRAINT FK_Vehicle_Approver  FOREIGN KEY (ApprovedBy) REFERENCES Account(AccountID)
);
GO

-- ============================================================
-- 7. Unit (Đơn vị tính)
-- ============================================================
CREATE TABLE Unit (
                      UnitID      INT             IDENTITY(1,1)   PRIMARY KEY,
                      UnitName    NVARCHAR(50)    NOT NULL         UNIQUE  -- Giờ, Ngày, Tháng, Lượt...
);
GO

-- ============================================================
-- 8. Service (Dịch vụ)
-- ============================================================
CREATE TABLE Service (
                         ServiceID   INT             IDENTITY(1,1)   PRIMARY KEY,
                         ServiceName NVARCHAR(100)   NOT NULL         UNIQUE,
                         ServiceType NVARCHAR(50)    NULL,                    -- Điện, Nước, Gửi xe, Vệ sinh,...
                         UnitPrice   DECIMAL(18,2)   NOT NULL         DEFAULT 0,
                         UnitID      INT             NOT NULL,
                         Status      BIT             NOT NULL         DEFAULT 1,

                         CONSTRAINT FK_Service_Unit FOREIGN KEY (UnitID) REFERENCES Unit(UnitID)
);
GO

-- ============================================================
-- 9. Utility (Tiện ích)
-- ============================================================
CREATE TABLE Utility (
                         UtilityID   INT             IDENTITY(1,1)   PRIMARY KEY,
                         UtilityName NVARCHAR(100)   NOT NULL         UNIQUE,
                         Description NVARCHAR(255)   NULL,
                         Status      BIT             NOT NULL         DEFAULT 1   -- 1: Hoạt động | 0: Dừng
);
GO

-- ============================================================
-- 10. UtilityPrice (Cấu hình bảng giá tiện ích)
-- ============================================================
CREATE TABLE UtilityPrice (
                              UtilityPriceID  INT             IDENTITY(1,1)   PRIMARY KEY,
                              UtilityID       INT             NOT NULL,
                              UnitID          INT             NOT NULL,
                              Price           DECIMAL(18,2)   NOT NULL         DEFAULT 0,

                              CONSTRAINT FK_UtilityPrice_Utility FOREIGN KEY (UtilityID) REFERENCES Utility(UtilityID),
                              CONSTRAINT FK_UtilityPrice_Unit    FOREIGN KEY (UnitID)    REFERENCES Unit(UnitID)
);
GO

-- ============================================================
-- 11. UtilityResource (Tài nguyên cụ thể của tiện ích)
-- ============================================================
CREATE TABLE UtilityResource (
                                 ResourceID      INT             IDENTITY(1,1)   PRIMARY KEY,
                                 UtilityID       INT             NOT NULL,
                                 ResourceName    NVARCHAR(100)   NOT NULL,
                                 Location        NVARCHAR(150)   NULL,
                                 Status          BIT             NOT NULL         DEFAULT 1,  -- 1: Sẵn sàng | 0: Bảo trì

                                 CONSTRAINT FK_UtilityResource_Utility FOREIGN KEY (UtilityID) REFERENCES Utility(UtilityID)
);
GO

-- ============================================================
-- 12. UtilityBooking (Đặt tiện ích)
-- ============================================================
CREATE TABLE UtilityBooking (
                                BookingID       INT             IDENTITY(1,1)   PRIMARY KEY,
                                ProfileID       INT             NOT NULL,
                                ResourceID      INT             NOT NULL,
                                UtilityPriceID  INT             NOT NULL,
                                StartTime       DATETIME        NOT NULL,
                                EndTime         DATETIME        NOT NULL,
                                TotalPrice      DECIMAL(18,2)   NOT NULL         DEFAULT 0,
                                Status          TINYINT         NOT NULL         DEFAULT 0,  -- 0: PENDING | 1: APPROVED | 2: REJECTED | 3: CANCELED
                                ApprovedBy      INT             NULL,
                                CreatedAt       DATETIME        NOT NULL         DEFAULT GETDATE(),
                                CanceledAt      DATETIME        NULL,
                                CancelReason    NVARCHAR(255)   NULL,

                                CONSTRAINT FK_Booking_Profile       FOREIGN KEY (ProfileID)      REFERENCES Profile(ProfileID),
                                CONSTRAINT FK_Booking_Resource      FOREIGN KEY (ResourceID)     REFERENCES UtilityResource(ResourceID),
                                CONSTRAINT FK_Booking_UtilityPrice  FOREIGN KEY (UtilityPriceID) REFERENCES UtilityPrice(UtilityPriceID),
                                CONSTRAINT FK_Booking_Approver      FOREIGN KEY (ApprovedBy)     REFERENCES Account(AccountID)
);
GO

-- ============================================================
-- 13. Bill (Hóa đơn tổng)
-- ============================================================
CREATE TABLE Bill (
                      BillID      INT             IDENTITY(1,1)   PRIMARY KEY,
                      ApartmentID INT             NOT NULL,
                      CreatedBy   INT             NOT NULL,
                      BillMonth   TINYINT         NOT NULL,                    -- 1–12
                      BillYear    SMALLINT        NOT NULL,
                      TotalAmount DECIMAL(18,2)   NOT NULL         DEFAULT 0,
                      Status      TINYINT         NOT NULL         DEFAULT 0,  -- 0: UNPAID | 1: PAID | 2: OVERDUE
                      DueDate     DATETIME        NOT NULL,                    -- Hạn thanh toán
                      CreatedDate DATETIME        NOT NULL         DEFAULT GETDATE(),
                      PaidDate    DATETIME        NULL,

                      CONSTRAINT FK_Bill_Apartment  FOREIGN KEY (ApartmentID) REFERENCES Apartment(ApartmentID),
                      CONSTRAINT FK_Bill_CreatedBy  FOREIGN KEY (CreatedBy)   REFERENCES Account(AccountID),
                      CONSTRAINT UQ_Bill_Month      UNIQUE (ApartmentID, BillMonth, BillYear)
);
GO

-- ============================================================
-- 14. BillDetail (Chi tiết hóa đơn)
-- ============================================================
CREATE TABLE BillDetail (
                            BillDetailID    BIGINT          IDENTITY(1,1)   PRIMARY KEY,
                            BillID          INT             NOT NULL,
                            ServiceID       INT             NOT NULL,
                            BookingID       INT             NULL,
                            Quantity        DECIMAL(10,2)   NOT NULL         DEFAULT 1,
                            Description     NVARCHAR(255)   NULL,
                            Amount          DECIMAL(18,2)   NOT NULL         DEFAULT 0,

                            CONSTRAINT FK_BillDetail_Bill       FOREIGN KEY (BillID)    REFERENCES Bill(BillID),
                            CONSTRAINT FK_BillDetail_Service    FOREIGN KEY (ServiceID) REFERENCES Service(ServiceID),
                            CONSTRAINT FK_BillDetail_Booking    FOREIGN KEY (BookingID) REFERENCES UtilityBooking(BookingID)
);
GO

-- ============================================================
-- 15. PaymentMethod (Phương thức thanh toán)
-- ============================================================
CREATE TABLE PaymentMethod (
                               MethodID    INT             IDENTITY(1,1)   PRIMARY KEY,
                               MethodName  NVARCHAR(100)   NOT NULL         UNIQUE,
                               IsOnline    BIT             NOT NULL         DEFAULT 1,  -- 1: Điện tử | 0: Thủ công
                               Status      BIT             NOT NULL         DEFAULT 1
);
GO

-- ============================================================
-- 16. Payment (Thanh toán)
-- ============================================================
CREATE TABLE Payment (
                         PaymentID       INT             IDENTITY(1,1)   PRIMARY KEY,
                         BillID          INT             NOT NULL,
                         PaidBy          INT             NOT NULL,
                         MethodID        INT             NOT NULL,
                         TransactionCode VARCHAR(100)    NULL             UNIQUE,
                         Amount          DECIMAL(18,2)   NOT NULL         CHECK (Amount > 0),
                         PaymentDate     DATETIME        NOT NULL         DEFAULT GETDATE(),
                         Status          TINYINT         NOT NULL         DEFAULT 0,  -- 0: PENDING | 1: SUCCESS | 2: FAILED

                         CONSTRAINT FK_Payment_Bill          FOREIGN KEY (BillID)    REFERENCES Bill(BillID),
                         CONSTRAINT FK_Payment_PaidBy        FOREIGN KEY (PaidBy)    REFERENCES Account(AccountID),
                         CONSTRAINT FK_Payment_Method        FOREIGN KEY (MethodID)  REFERENCES PaymentMethod(MethodID)
);
GO

-- ============================================================
-- 17. MaintenanceRequest (Yêu cầu bảo trì)
-- ============================================================
CREATE TABLE MaintenanceRequest (
                                    RequestID   INT             IDENTITY(1,1)   PRIMARY KEY,
                                    ProfileID   INT             NOT NULL,
                                    ApartmentID INT             NOT NULL,
                                    Title       NVARCHAR(100)   NOT NULL,
                                    Description NVARCHAR(MAX)   NOT NULL,
                                    ImageURL    NVARCHAR(500)   NULL,                        -- Ảnh đính kèm sự cố
                                    RequestDate DATETIME        NOT NULL         DEFAULT GETDATE(),
                                    Status      TINYINT         NOT NULL         DEFAULT 0,  -- 0: PENDING | 1: ASSIGNED | 2: COMPLETED

                                    CONSTRAINT FK_MaintRequest_Profile    FOREIGN KEY (ProfileID)   REFERENCES Profile(ProfileID),
                                    CONSTRAINT FK_MaintRequest_Apartment  FOREIGN KEY (ApartmentID) REFERENCES Apartment(ApartmentID)
);
GO

-- ============================================================
-- 18. MaintenanceTask (Phân công công việc)
-- ============================================================
CREATE TABLE MaintenanceTask (
                                 TaskID          INT         IDENTITY(1,1)   PRIMARY KEY,
                                 RequestID       INT         NOT NULL         UNIQUE,     -- 1 Request → 1 Task
                                 StaffID         INT         NOT NULL,
                                 AssignedBy      INT         NOT NULL,
                                 AssignedDate    DATETIME    NOT NULL         DEFAULT GETDATE(),
                                 Deadline        DATETIME    NULL,                        -- Hạn hoàn thành
                                 Status          TINYINT     NOT NULL         DEFAULT 1,  -- 1: ASSIGNED | 2: IN_PROGRESS | 3: COMPLETED

                                 CONSTRAINT FK_MaintTask_Request     FOREIGN KEY (RequestID)  REFERENCES MaintenanceRequest(RequestID),
                                 CONSTRAINT FK_MaintTask_Staff       FOREIGN KEY (StaffID)    REFERENCES Account(AccountID),
                                 CONSTRAINT FK_MaintTask_AssignedBy  FOREIGN KEY (AssignedBy) REFERENCES Account(AccountID)
);
GO

-- ============================================================
-- 19. MaintenanceReport (Báo cáo tiến độ)
-- ============================================================
CREATE TABLE MaintenanceReport (
                                   ReportID        BIGINT          IDENTITY(1,1)   PRIMARY KEY,
                                   TaskID          INT             NOT NULL,
                                   ReportContent   NVARCHAR(MAX)   NOT NULL,
                                   ProgressPercent TINYINT         NOT NULL         CHECK (ProgressPercent BETWEEN 0 AND 100),
                                   CreatedAt       DATETIME        NOT NULL         DEFAULT GETDATE(),

                                   CONSTRAINT FK_MaintReport_Task FOREIGN KEY (TaskID) REFERENCES MaintenanceTask(TaskID)
);
GO

-- ============================================================
-- 20. Notification (Thông báo gốc)
-- ============================================================
CREATE TABLE Notification (
                              NotificationID      BIGINT          IDENTITY(1,1)   PRIMARY KEY,
                              Title               NVARCHAR(100)   NOT NULL,
                              Content             NVARCHAR(MAX)   NOT NULL,
                              NotificationType    TINYINT         NOT NULL         DEFAULT 3,  -- 1: Hệ thống | 2: Hóa đơn | 3: Tin chung
                              CreatedBy           INT             NOT NULL,
                              CreatedAt           DATETIME        NOT NULL         DEFAULT GETDATE(),
                              RelatedEntityType   NVARCHAR(50)    NULL,            -- Bill, Booking, Request, Vehicle,...
                              RelatedEntityID     INT             NULL,            -- ID bản ghi liên quan

                              CONSTRAINT FK_Notification_CreatedBy FOREIGN KEY (CreatedBy) REFERENCES Account(AccountID)
);
GO

-- ============================================================
-- 21. AccountNotification (Trạng thái nhận thông báo)
-- ============================================================
CREATE TABLE AccountNotification (
                                     ID              BIGINT      IDENTITY(1,1)   PRIMARY KEY,
                                     NotificationID  BIGINT      NOT NULL,
                                     AccountID       INT         NOT NULL,
                                     IsRead          BIT         NOT NULL         DEFAULT 0,
                                     ReadAt          DATETIME    NULL,

                                     CONSTRAINT FK_AccNotif_Notification FOREIGN KEY (NotificationID) REFERENCES Notification(NotificationID),
                                     CONSTRAINT FK_AccNotif_Account      FOREIGN KEY (AccountID)      REFERENCES Account(AccountID),
                                     CONSTRAINT UQ_AccNotif              UNIQUE (NotificationID, AccountID)
);
GO

-- ============================================================
-- 22. SystemLog (Nhật ký hệ thống)
-- ============================================================
CREATE TABLE SystemLog (
                           SystemLogID BIGINT          IDENTITY(1,1)   PRIMARY KEY,
                           AccountID   INT             NULL,                        -- NULL nếu là hành động hệ thống tự động
                           Action      NVARCHAR(100)   NOT NULL,                   -- LOGIN, CREATE_BILL, LOCK_ACCOUNT,...
                           EntityType  NVARCHAR(50)    NULL,                       -- Account, Bill, Booking,...
                           EntityID    INT             NULL,
                           IPAddress   VARCHAR(45)     NULL,                       -- Hỗ trợ IPv6
                           CreatedAt   DATETIME        NOT NULL         DEFAULT GETDATE(),

                           CONSTRAINT FK_SystemLog_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
);
GO