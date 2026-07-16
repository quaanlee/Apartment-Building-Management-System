-- ============================================================
-- MOCK DATA: Maintenance Request and related tables
-- Date: 2026-07-07
-- Description: Inserts or updates test data to display maintenance requests on Manager/Staff portal
-- ============================================================

USE ABMS;
GO

-- 1. Roles
SET IDENTITY_INSERT Role ON;
IF NOT EXISTS (SELECT 1 FROM Role WHERE RoleID = 1) INSERT INTO Role (RoleID, RoleName) VALUES (1, 'ADMIN');
IF NOT EXISTS (SELECT 1 FROM Role WHERE RoleID = 2) INSERT INTO Role (RoleID, RoleName) VALUES (2, 'MANAGER');
IF NOT EXISTS (SELECT 1 FROM Role WHERE RoleID = 3) INSERT INTO Role (RoleID, RoleName) VALUES (3, 'RESIDENT');
IF NOT EXISTS (SELECT 1 FROM Role WHERE RoleID = 4) INSERT INTO Role (RoleID, RoleName) VALUES (4, 'MAINTENANCE_STAFF');
SET IDENTITY_INSERT Role OFF;
GO

-- 2. Accounts
IF EXISTS (SELECT 1 FROM Account WHERE AccountID = 1)
    UPDATE Account SET Username = 'admin1@gmail.com', Password = '123', RoleID = 1, Status = 1 WHERE AccountID = 1;
ELSE
BEGIN
    SET IDENTITY_INSERT Account ON;
    INSERT INTO Account (AccountID, Username, Password, RoleID, Status) VALUES (1, 'admin1@gmail.com', '123', 1, 1);
    SET IDENTITY_INSERT Account OFF;
END

IF EXISTS (SELECT 1 FROM Account WHERE AccountID = 2)
    UPDATE Account SET Username = 'manager1@gmail.com', Password = '123', RoleID = 2, Status = 1 WHERE AccountID = 2;
ELSE
BEGIN
    SET IDENTITY_INSERT Account ON;
    INSERT INTO Account (AccountID, Username, Password, RoleID, Status) VALUES (2, 'manager1@gmail.com', '123', 2, 1);
    SET IDENTITY_INSERT Account OFF;
END

IF EXISTS (SELECT 1 FROM Account WHERE AccountID = 3)
    UPDATE Account SET Username = 'resident1@gmail.com', Password = '123', RoleID = 3, Status = 1 WHERE AccountID = 3;
ELSE
BEGIN
    SET IDENTITY_INSERT Account ON;
    INSERT INTO Account (AccountID, Username, Password, RoleID, Status) VALUES (3, 'resident1@gmail.com', '123', 3, 1);
    SET IDENTITY_INSERT Account OFF;
END

IF EXISTS (SELECT 1 FROM Account WHERE AccountID = 4)
    UPDATE Account SET Username = 'worker1@gmail.com', Password = '123', RoleID = 4, Status = 1 WHERE AccountID = 4;
ELSE
BEGIN
    SET IDENTITY_INSERT Account ON;
    INSERT INTO Account (AccountID, Username, Password, RoleID, Status) VALUES (4, 'worker1@gmail.com', '123', 4, 1);
    SET IDENTITY_INSERT Account OFF;
END

IF EXISTS (SELECT 1 FROM Account WHERE AccountID = 5)
    UPDATE Account SET Username = 'worker2@gmail.com', Password = '123', RoleID = 4, Status = 1 WHERE AccountID = 5;
ELSE
BEGIN
    SET IDENTITY_INSERT Account ON;
    INSERT INTO Account (AccountID, Username, Password, RoleID, Status) VALUES (5, 'worker2@gmail.com', '123', 4, 1);
    SET IDENTITY_INSERT Account OFF;
END
GO

-- 3. Apartments
IF EXISTS (SELECT 1 FROM Apartment WHERE ApartmentID = 1)
    UPDATE Apartment SET ApartmentNumber = 'A-101', Floor = 1, Area = 75.50, RoomType = '2 Bedrooms', Status = 1 WHERE ApartmentID = 1;
ELSE
BEGIN
    SET IDENTITY_INSERT Apartment ON;
    INSERT INTO Apartment (ApartmentID, ApartmentNumber, Floor, Area, RoomType, Status) VALUES (1, 'A-101', 1, 75.50, '2 Bedrooms', 1);
    SET IDENTITY_INSERT Apartment OFF;
END

IF EXISTS (SELECT 1 FROM Apartment WHERE ApartmentID = 2)
    UPDATE Apartment SET ApartmentNumber = 'B-202', Floor = 2, Area = 90.00, RoomType = '3 Bedrooms', Status = 1 WHERE ApartmentID = 2;
ELSE
BEGIN
    SET IDENTITY_INSERT Apartment ON;
    INSERT INTO Apartment (ApartmentID, ApartmentNumber, Floor, Area, RoomType, Status) VALUES (2, 'B-202', 2, 90.00, '3 Bedrooms', 1);
    SET IDENTITY_INSERT Apartment OFF;
END
GO

-- Unlink old staff records from Profile table if they exist to avoid Account mapping confusion
IF EXISTS (SELECT 1 FROM Profile WHERE ProfileID IN (1, 3, 4))
BEGIN
    UPDATE Profile SET AccountID = NULL WHERE ProfileID IN (1, 3, 4);
END

-- 4. Resident Profiles
IF EXISTS (SELECT 1 FROM Profile WHERE ProfileID = 2)
    UPDATE Profile SET AccountID = 3, ApartmentID = 1, FullName = N'Tran Duc Nam (Resident)', Gender = N'Nam', PhoneNumber = '0987654321', Email = 'namtd@gmail.com', CitizenID = '123456789002', IsHouseholdOwner = 1, ResidentStatus = 1 WHERE ProfileID = 2;
ELSE
BEGIN
    SET IDENTITY_INSERT Profile ON;
    INSERT INTO Profile (ProfileID, AccountID, ApartmentID, FullName, Gender, PhoneNumber, Email, CitizenID, IsHouseholdOwner, ResidentStatus) VALUES (2, 3, 1, N'Tran Duc Nam (Resident)', N'Nam', '0987654321', 'namtd@gmail.com', '123456789002', 1, 1);
    SET IDENTITY_INSERT Profile OFF;
END
GO

-- 4.1 Employee Profiles
IF EXISTS (SELECT 1 FROM EmployeeProfile WHERE AccountID = 2)
    UPDATE EmployeeProfile SET FullName = N'Nguyen Van Ly (Manager)', Gender = 1, PhoneNumber = '0912345678', Email = 'manager1@gmail.com', Address = N'Phòng BQL, Tầng 1', Status = 1 WHERE AccountID = 2;
ELSE
    INSERT INTO EmployeeProfile (AccountID, FullName, Gender, PhoneNumber, Email, Address, Status) VALUES (2, N'Nguyen Van Ly (Manager)', 1, '0912345678', 'manager1@gmail.com', N'Phòng BQL, Tầng 1', 1);

IF EXISTS (SELECT 1 FROM EmployeeProfile WHERE AccountID = 4)
    UPDATE EmployeeProfile SET FullName = N'Le Van Sua (Thợ ống nước)', Gender = 1, PhoneNumber = '0922222222', Email = 'worker1@gmail.com', Address = N'Phòng kỹ thuật, Tầng hầm B1', Status = 1 WHERE AccountID = 4;
ELSE
    INSERT INTO EmployeeProfile (AccountID, FullName, Gender, PhoneNumber, Email, Address, Status) VALUES (4, N'Le Van Sua (Thợ ống nước)', 1, '0922222222', 'worker1@gmail.com', N'Phòng kỹ thuật, Tầng hầm B1', 1);

IF EXISTS (SELECT 1 FROM EmployeeProfile WHERE AccountID = 5)
    UPDATE EmployeeProfile SET FullName = N'Pham Minh Dien (Thợ sửa điện)', Gender = 1, PhoneNumber = '0933333333', Email = 'worker2@gmail.com', Address = N'Phòng kỹ thuật, Tầng hầm B1', Status = 1 WHERE AccountID = 5;
ELSE
    INSERT INTO EmployeeProfile (AccountID, FullName, Gender, PhoneNumber, Email, Address, Status) VALUES (5, N'Pham Minh Dien (Thợ sửa điện)', 1, '0933333333', 'worker2@gmail.com', N'Phòng kỹ thuật, Tầng hầm B1', 1);
GO

-- 5. Maintenance Requests
IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 1)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 1, Title = N'Rò rỉ đường ống nước phòng tắm', Description = N'Vòi sen tắm nhà mình bị rò nước liên tục nhỏ giọt rất lãng phí, cần kỹ thuật viên hỗ trợ kiểm tra khóa van hoặc thay gioăng cao su mới giúp mình.', Status = 0, RequestDate = DATEADD(hour, -5, GETDATE()) WHERE RequestID = 1;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (1, 2, 1, N'Rò rỉ đường ống nước phòng tắm', N'Vòi sen tắm nhà mình bị rò nước liên tục nhỏ giọt rất lãng phí, cần kỹ thuật viên hỗ trợ kiểm tra khóa van hoặc thay gioăng cao su mới giúp mình.', 0, DATEADD(hour, -5, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END

IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 2)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 1, Title = N'Ổ cắm điện phòng khách bị cháy chập', Description = N'Hôm qua nhà mình cắm bếp lẩu thì nghe tiếng nổ nhẹ và đánh lửa khét lẹt ở ổ cắm góc tivi, hiện tại ổ cắm đó đã bị cháy đen và không có điện nữa.', Status = 1, RequestDate = DATEADD(hour, -2, GETDATE()) WHERE RequestID = 2;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (2, 2, 1, N'Ổ cắm điện phòng khách bị cháy chập', N'Hôm qua nhà mình cắm bếp lẩu thì nghe tiếng nổ nhẹ và đánh lửa khét lẹt ở ổ cắm góc tivi, hiện tại ổ cắm đó đã bị cháy đen và không có điện nữa.', 1, DATEADD(hour, -2, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END

IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 3)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 1, Title = N'Tay nắm chốt cửa ban công bị kẹt', Description = N'Tay nắm cửa ban công hướng Đông bị kẹt cứng không thể gạt chốt khóa an toàn được, ban đêm gió lùa mạnh rất nguy hiểm.', Status = 0, RequestDate = DATEADD(day, -1, GETDATE()) WHERE RequestID = 3;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (3, 2, 1, N'Tay nắm chốt cửa ban công bị kẹt', N'Tay nắm cửa ban công hướng Đông bị kẹt cứng không thể gạt chốt khóa an toàn được, ban đêm gió lùa mạnh rất nguy hiểm.', 0, DATEADD(day, -1, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END

IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 4)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 1, Title = N'Điều hòa phòng ngủ chính không lạnh', Description = N'Điều hòa Daikin ở phòng ngủ master bật 16 độ nhưng chỉ có gió nhẹ không lạnh, có tiếng kêu rè rè khó chịu.', Status = 0, RequestDate = DATEADD(day, -2, GETDATE()) WHERE RequestID = 4;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (4, 2, 1, N'Điều hòa phòng ngủ chính không lạnh', N'Điều hòa Daikin ở phòng ngủ master bật 16 độ nhưng chỉ có gió nhẹ không lạnh, có tiếng kêu rè rè khó chịu.', 0, DATEADD(day, -2, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END

IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 5)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 1, Title = N'Bóng đèn hành lang bị cháy', Description = N'Bóng đèn downlight ở hành lang trước cửa căn hộ bị nhấp nháy liên tục rồi tắt hẳn, cần thay bóng mới.', Status = 0, RequestDate = DATEADD(day, -3, GETDATE()) WHERE RequestID = 5;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (5, 2, 1, N'Bóng đèn hành lang bị cháy', N'Bóng đèn downlight ở hành lang trước cửa căn hộ bị nhấp nháy liên tục rồi tắt hẳn, cần thay bóng mới.', 0, DATEADD(day, -3, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END

IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 6)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 2, Title = N'Kẹt vòi xịt vệ sinh toilet phụ', Description = N'Vòi xịt vệ sinh phòng tắm phụ bị kẹt nút nhấn, nước chảy liên tục không ngắt được phải khóa van góc.', Status = 0, RequestDate = DATEADD(day, -1, GETDATE()) WHERE RequestID = 6;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (6, 2, 2, N'Kẹt vòi xịt vệ sinh toilet phụ', N'Vòi xịt vệ sinh phòng tắm phụ bị kẹt nút nhấn, nước chảy liên tục không ngắt được phải khóa van góc.', 0, DATEADD(day, -1, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END

IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 7)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 2, Title = N'Hỏng công tắc bình nóng lạnh', Description = N'Công tắc bật bình nóng lạnh phòng tắm chính bị lỏng lẻo gạt lên không sáng đèn và không nóng.', Status = 0, RequestDate = DATEADD(day, -4, GETDATE()) WHERE RequestID = 7;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (7, 2, 2, N'Hỏng công tắc bình nóng lạnh', N'Công tắc bật bình nóng lạnh phòng tắm chính bị lỏng lẻo gạt lên không sáng đèn và không nóng.', 0, DATEADD(day, -4, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END
GO

-- 6. Maintenance Tasks
-- Task 1: assigned to Staff 4, Status 1 (Assigned)
IF EXISTS (SELECT 1 FROM MaintenanceTask WHERE TaskID = 1)
    UPDATE MaintenanceTask SET RequestID = 2, StaffID = 4, AssignedBy = 2, AssignedDate = DATEADD(hour, -2, GETDATE()), Deadline = DATEADD(day, 2, GETDATE()), Status = 1 WHERE TaskID = 1;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceTask ON;
    INSERT INTO MaintenanceTask (TaskID, RequestID, StaffID, AssignedBy, AssignedDate, Deadline, Status) VALUES (1, 2, 4, 2, DATEADD(hour, -2, GETDATE()), DATEADD(day, 2, GETDATE()), 1);
    SET IDENTITY_INSERT MaintenanceTask OFF;
END
GO

-- 7. Maintenance Reports
-- (No mock reports since Task 1 is in Assigned status with 0% progress)

-- 8. Notifications Mock Data
SET IDENTITY_INSERT Notification ON;

IF EXISTS (SELECT 1 FROM Notification WHERE NotificationID = 1)
    UPDATE Notification SET Title = N'Công việc mới được phân công', Content = N'Bạn được phân công xử lý sự cố: Ổ cắm điện phòng khách bị cháy chập.', CreatedBy = 2, CreatedAt = DATEADD(hour, -1, GETDATE()), RelatedEntityType = 'MaintenanceTask', ReceiverID = 4 WHERE NotificationID = 1;
ELSE
    INSERT INTO Notification (NotificationID, Title, Content, NotificationType, CreatedBy, CreatedAt, RelatedEntityType, ReceiverID)
    VALUES (1, N'Công việc mới được phân công', N'Bạn được phân công xử lý sự cố: Ổ cắm điện phòng khách bị cháy chập.', 3, 2, DATEADD(hour, -1, GETDATE()), 'MaintenanceTask', 4);

SET IDENTITY_INSERT Notification OFF;
GO

-- 9. Account Notifications Mock Data
SET IDENTITY_INSERT AccountNotification ON;

IF EXISTS (SELECT 1 FROM AccountNotification WHERE ID = 1)
    UPDATE AccountNotification SET NotificationID = 1, AccountID = 4, IsRead = 0, ReadAt = NULL WHERE ID = 1;
ELSE
    INSERT INTO AccountNotification (ID, NotificationID, AccountID, IsRead, ReadAt)
    VALUES (1, 1, 4, 0, NULL);

SET IDENTITY_INSERT AccountNotification OFF;
GO
