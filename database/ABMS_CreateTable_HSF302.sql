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
    ImageURL    NVARCHAR(500)   NULL,
    Status      BIT             NOT NULL         DEFAULT 1,  -- 1: Hoạt động | 0: Dừng
    Type        BIT             NOT NULL         DEFAULT 1   -- 1: RESERVABLE | 0: FREE_USE
);
GO

-- ============================================================
-- 9.1 UtilityResource (Tài nguyên cụ thể của tiện ích)
-- ============================================================
CREATE TABLE UtilityResource (
    ResourceID      INT             IDENTITY(1,1)   PRIMARY KEY,
    UtilityID       INT             NOT NULL,
    ResourceName    NVARCHAR(100)   NOT NULL,
    Location        NVARCHAR(150)   NULL,
    Description     NVARCHAR(255)   NULL,
    Status          BIT             NOT NULL         DEFAULT 1,  -- 1: Sẵn sàng | 0: Bảo trì

    CONSTRAINT FK_UtilityResource_Utility FOREIGN KEY (UtilityID) REFERENCES Utility(UtilityID)
);
GO

-- ============================================================
-- 9.2 UtilityPrice (Cấu hình bảng giá tiện ích)
-- ============================================================
CREATE TABLE UtilityPrice (
    UtilityPriceID  INT             IDENTITY(1,1)   PRIMARY KEY,
    ResourceID      INT             NOT NULL,
    UnitID          INT             NOT NULL,
    Price           DECIMAL(18,2)   NOT NULL         DEFAULT 0,

    CONSTRAINT FK_UtilityPrice_UtilityResource FOREIGN KEY (ResourceID) REFERENCES UtilityResource(ResourceID),
    CONSTRAINT FK_UtilityPrice_Unit            FOREIGN KEY (UnitID)     REFERENCES Unit(UnitID)
);
GO

-- ============================================================
-- 9.3 UtilityImage (Ảnh Tiện ích)
-- ============================================================
CREATE TABLE UtilityImage (
    ImageID     INT           IDENTITY(1,1)   PRIMARY KEY,
    ResourceID  INT           NOT NULL,                      -- Khóa ngoại liên kết với bảng UtilityResource
    ImageURL    NVARCHAR(500) NOT NULL,                      -- Đường dẫn lưu file ảnh (hoặc link CDN)
    Caption     NVARCHAR(255) NULL,                          -- Mô tả ngắn cho bức ảnh (ví dụ: "Góc chính diện", "View ban đêm")
    IsPrimary   BIT           NOT NULL         DEFAULT 0,    -- 1: Ảnh đại diện/Ảnh chính | 0: Ảnh phụ
    CreatedDate DATETIME      NOT NULL         DEFAULT GETDATE(), -- Ngày đăng ảnh
    
    -- Tạo ràng buộc khóa ngoại để đảm bảo toàn vẹn dữ liệu
    CONSTRAINT FK_UtilityImage_UtilityResource FOREIGN KEY (ResourceID) 
        REFERENCES UtilityResource(ResourceID) 
        ON DELETE CASCADE -- Nếu xóa tài nguyên tiện ích, tự động xóa hết ảnh liên quan
);
GO

-- ============================================================
-- 10. UtilityBooking (Đặt tiện ích)
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
    CancelledAt      DATETIME        NULL,
    CancelReason    NVARCHAR(255)   NULL,
	PaymentStatus   BIT             NOT NULL         DEFAULT 0,

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
    ReceiverID     INT             NULL,            -- ID bản ghi liên quan
	Recipient           NVARCHAR(50)    NULL          -- ĐỐI ĐƯỢNG NHẬN: TOÀN BỘ, CHỦ HỘ, NHÂN VIÊN BẢO TRÌ,TÙY CHỌN CƯ DÂN RIÊNG,....
    CONSTRAINT FK_Notification_CreatedBy FOREIGN KEY (CreatedBy) REFERENCES Account(AccountID),
	CONSTRAINT FK_Notification_ReceiverID FOREIGN KEY (ReceiverID) REFERENCES Account(AccountID)
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

-- ===============THÊM BẢNG=========================


-- ========================================================================
-- 19.1 Bảng lưu ảnh báo cáo tiến độ bảo trì
-- ========================================================================
CREATE TABLE MaintenanceReportImage (
    ImageID         BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    ReportID        BIGINT          NOT NULL,               -- Khóa ngoại liên kết tới MaintenanceReport (Dùng BIGINT giống ReportID)
    ImageURL       NVARCHAR(500)   NOT NULL,               -- Đường dẫn lưu tệp ảnh
    Caption         NVARCHAR(255)       NULL,               -- Chú thích ảnh (VD: "Đang lắp ống", "Đã hàn xong mối nối")

    -- Định nghĩa khóa ngoại
    CONSTRAINT FK_ReportImage_Report FOREIGN KEY (ReportID) REFERENCES MaintenanceReport(ReportID)
        ON DELETE CASCADE -- Nếu báo cáo tiến độ bị xóa, tự động dọn dẹp các ảnh liên quan
);
GO
-- ============================================================
-- 17.1 MaintenanceRequestImage (Ảnh tình trạng vấn đề cần bảo trì)
-- ============================================================
CREATE TABLE MaintenanceRequestImage (
    ImageID         INT             IDENTITY(1,1)   PRIMARY KEY,
    RequestID       INT             NOT NULL,               -- Khóa ngoại liên kết tới MaintenanceRequest
    ImageURL       NVARCHAR(500)   NOT NULL,               -- Đường dẫn file ảnh tình trạng
    Description     NVARCHAR(255)       NULL,               -- Ghi chú ngắn gọn cho bức ảnh (VD: "Đã thay vòi nước mới")

    -- Định nghĩa khóa ngoại
    CONSTRAINT FK_MaintImage_Request FOREIGN KEY (RequestID) REFERENCES MaintenanceRequest(RequestID)
        ON DELETE CASCADE -- Nếu yêu cầu bảo trì bị xóa, tự động xóa các ảnh liên quan
);
GO
-- ============================================================
-- 23. ApartmentImage (Ảnh căn hộ)
-- ============================================================
CREATE TABLE ApartmentImage (
    ImageID         INT             IDENTITY(1,1)   PRIMARY KEY,
    ApartmentID     INT             NOT NULL,               -- Khóa ngoại liên kết tới bảng Apartment
    ImageURL       VARCHAR(500)    NOT NULL,               -- Đường dẫn lưu tệp ảnh (URL hoặc đường dẫn thư mục)
    ImageTitle      NVARCHAR(100)       NULL,               -- Tiêu đề ảnh (VD: Phòng khách, Ban công...)
    IsPrimary       BIT             NOT NULL        DEFAULT 0, -- 1: Ảnh đại diện/Ảnh chính | 0: Ảnh phụ
    UploadedAt      DATETIME        NOT NULL        DEFAULT GETDATE(), -- Ngày giờ tải ảnh lên

    -- Định nghĩa khóa ngoại
    CONSTRAINT FK_Image_Apartment FOREIGN KEY (ApartmentID) REFERENCES Apartment(ApartmentID) 
        ON DELETE CASCADE -- Nếu căn hộ bị xóa, tự động xóa thông tin ảnh liên quan
);
GO
-- ============================================================
-- 24. ApartmentPriceHistory (Lịch sử giá căn hộ)
-- ============================================================
CREATE TABLE ApartmentPriceHistory (
    PriceHistoryID  INT             IDENTITY(1,1)   PRIMARY KEY,
    ApartmentID     INT             NOT NULL,               -- Khóa ngoại liên kết tới Apartment
    
    Price           DECIMAL(18, 2)  NOT NULL,               -- Mức giá tại thời điểm áp dụng
    
    EffectiveDate   DATE            NOT NULL,               -- Ngày bắt đầu áp dụng mức giá này
    EndDate         DATE                NULL,               -- Ngày kết thúc áp dụng (NULL nghĩa là giá hiện tại)
    
    CreatedAt       DATETIME        NOT NULL        DEFAULT GETDATE(), -- Ngày hệ thống ghi nhận việc đổi giá
    Note            NVARCHAR(255)       NULL,               -- Lý do đổi giá (VD: "Tăng giá theo thị trường", "Sửa sang lại phòng")

    -- Định nghĩa các ràng buộc
    CONSTRAINT FK_PriceHistory_Apartment FOREIGN KEY (ApartmentID) REFERENCES Apartment(ApartmentID) ON DELETE CASCADE,
    CONSTRAINT CHK_PriceHistory_Dates CHECK (EndDate IS NULL OR EndDate >= EffectiveDate),
    CONSTRAINT CHK_PriceHistory_Price CHECK (Price >= 0)
);
GO
-- ============================================================
-- 25. SalesContract (Hợp đồn mua bán)
-- ============================================================
CREATE TABLE SalesContract (
    SalesContractID   INT             IDENTITY(1,1)   PRIMARY KEY,
    ApartmentID       INT             NOT NULL,
    ProfileID         INT             NOT NULL,
    SellingPrice      DECIMAL(18, 2)  NOT NULL, -- Giá bán đứt căn hộ
    MaintenanceFee    DECIMAL(18, 2)  NOT NULL        DEFAULT 0, -- Phí bảo trì (thường là 2% giá trị căn hộ)
    HandoverDate      DATE                NULL, -- Ngày bàn giao nhà dự kiến/thực tế
    SignDate          DATE            NOT NULL        DEFAULT GETDATE(), -- Ngày ký hợp đồng

    PaymentStatus     TINYINT         NOT NULL        DEFAULT 0, -- 0: Chưa thanh toán | 1: Đang trả góp/theo đợt | 2: Đã thanh toán 100%
    Note              NVARCHAR(255)       NULL,
	CreatedAt       DATETIME        NOT NULL        DEFAULT GETDATE(), -- Ngày tạo hợp đồng
    CONSTRAINT FK_SalesContract_Apartment FOREIGN KEY (ApartmentID) REFERENCES Apartment(ApartmentID),
	CONSTRAINT FK_SalesContract_Profile FOREIGN KEY (ProfileID) REFERENCES Profile(ProfileID),
    CONSTRAINT CHK_SalesContract_Price CHECK (SellingPrice >= 0)
);
GO