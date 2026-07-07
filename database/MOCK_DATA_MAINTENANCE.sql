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

-- 4. Profiles
IF EXISTS (SELECT 1 FROM Profile WHERE ProfileID = 1)
    UPDATE Profile SET AccountID = 2, ApartmentID = NULL, FullName = N'Nguyen Van Ly (Manager)', Gender = N'Nam', PhoneNumber = '0912345678', Email = 'lynv@abms.com', CitizenID = '123456789001', IsHouseholdOwner = 1, ResidentStatus = 1 WHERE ProfileID = 1;
ELSE
BEGIN
    SET IDENTITY_INSERT Profile ON;
    INSERT INTO Profile (ProfileID, AccountID, ApartmentID, FullName, Gender, PhoneNumber, Email, CitizenID, IsHouseholdOwner, ResidentStatus) VALUES (1, 2, NULL, N'Nguyen Van Ly (Manager)', N'Nam', '0912345678', 'lynv@abms.com', '123456789001', 1, 1);
    SET IDENTITY_INSERT Profile OFF;
END

IF EXISTS (SELECT 1 FROM Profile WHERE ProfileID = 2)
    UPDATE Profile SET AccountID = 3, ApartmentID = 1, FullName = N'Tran Duc Nam (Resident)', Gender = N'Nam', PhoneNumber = '0987654321', Email = 'namtd@gmail.com', CitizenID = '123456789002', IsHouseholdOwner = 1, ResidentStatus = 1 WHERE ProfileID = 2;
ELSE
BEGIN
    SET IDENTITY_INSERT Profile ON;
    INSERT INTO Profile (ProfileID, AccountID, ApartmentID, FullName, Gender, PhoneNumber, Email, CitizenID, IsHouseholdOwner, ResidentStatus) VALUES (2, 3, 1, N'Tran Duc Nam (Resident)', N'Nam', '0987654321', 'namtd@gmail.com', '123456789002', 1, 1);
    SET IDENTITY_INSERT Profile OFF;
END

IF EXISTS (SELECT 1 FROM Profile WHERE ProfileID = 3)
    UPDATE Profile SET AccountID = 4, ApartmentID = NULL, FullName = N'Le Van Sua (Thợ ống nước)', Gender = N'Nam', PhoneNumber = '0922222222', Email = 'sualv@abms.com', CitizenID = '123456789003', IsHouseholdOwner = 0, ResidentStatus = 1 WHERE ProfileID = 3;
ELSE
BEGIN
    SET IDENTITY_INSERT Profile ON;
    INSERT INTO Profile (ProfileID, AccountID, ApartmentID, FullName, Gender, PhoneNumber, Email, CitizenID, IsHouseholdOwner, ResidentStatus) VALUES (3, 4, NULL, N'Le Van Sua (Thợ ống nước)', N'Nam', '0922222222', 'sualv@abms.com', '123456789003', 0, 1);
    SET IDENTITY_INSERT Profile OFF;
END

IF EXISTS (SELECT 1 FROM Profile WHERE ProfileID = 4)
    UPDATE Profile SET AccountID = 5, ApartmentID = NULL, FullName = N'Pham Minh Dien (Thợ sửa điện)', Gender = N'Nam', PhoneNumber = '0933333333', Email = 'dienpm@abms.com', CitizenID = '123456789004', IsHouseholdOwner = 0, ResidentStatus = 1 WHERE ProfileID = 4;
ELSE
BEGIN
    SET IDENTITY_INSERT Profile ON;
    INSERT INTO Profile (ProfileID, AccountID, ApartmentID, FullName, Gender, PhoneNumber, Email, CitizenID, IsHouseholdOwner, ResidentStatus) VALUES (4, 5, NULL, N'Pham Minh Dien (Thợ sửa điện)', N'Nam', '0933333333', 'dienpm@abms.com', '123456789004', 0, 1);
    SET IDENTITY_INSERT Profile OFF;
END
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
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 1, Title = N'Tay nắm chốt cửa ban công bị kẹt', Description = N'Tay nắm cửa ban công hướng Đông bị kẹt cứng không thể gạt chốt khóa an toàn được, ban đêm gió lùa mạnh rất nguy hiểm.', Status = 2, RequestDate = DATEADD(day, -1, GETDATE()) WHERE RequestID = 3;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (3, 2, 1, N'Tay nắm chốt cửa ban công bị kẹt', N'Tay nắm cửa ban công hướng Đông bị kẹt cứng không thể gạt chốt khóa an toàn được, ban đêm gió lùa mạnh rất nguy hiểm.', 2, DATEADD(day, -1, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END

IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 4)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 1, Title = N'Điều hòa phòng ngủ chính không lạnh', Description = N'Điều hòa Daikin ở phòng ngủ master bật 16 độ nhưng chỉ có gió nhẹ không lạnh, có tiếng kêu rè rè khó chịu.', Status = 1, RequestDate = DATEADD(day, -2, GETDATE()) WHERE RequestID = 4;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (4, 2, 1, N'Điều hòa phòng ngủ chính không lạnh', N'Điều hòa Daikin ở phòng ngủ master bật 16 độ nhưng chỉ có gió nhẹ không lạnh, có tiếng kêu rè rè khó chịu.', 1, DATEADD(day, -2, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END

IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 5)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 1, Title = N'Bóng đèn hành lang bị cháy', Description = N'Bóng đèn downlight ở hành lang trước cửa căn hộ bị nhấp nháy liên tục rồi tắt hẳn, cần thay bóng mới.', Status = 2, RequestDate = DATEADD(day, -3, GETDATE()) WHERE RequestID = 5;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (5, 2, 1, N'Bóng đèn hành lang bị cháy', N'Bóng đèn downlight ở hành lang trước cửa căn hộ bị nhấp nháy liên tục rồi tắt hẳn, cần thay bóng mới.', 2, DATEADD(day, -3, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END

IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 6)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 2, Title = N'Kẹt vòi xịt vệ sinh toilet phụ', Description = N'Vòi xịt vệ sinh phòng tắm phụ bị kẹt nút nhấn, nước chảy liên tục không ngắt được phải khóa van góc.', Status = 1, RequestDate = DATEADD(day, -1, GETDATE()) WHERE RequestID = 6;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (6, 2, 2, N'Kẹt vòi xịt vệ sinh toilet phụ', N'Vòi xịt vệ sinh phòng tắm phụ bị kẹt nút nhấn, nước chảy liên tục không ngắt được phải khóa van góc.', 1, DATEADD(day, -1, GETDATE()));
    SET IDENTITY_INSERT MaintenanceRequest OFF;
END

IF EXISTS (SELECT 1 FROM MaintenanceRequest WHERE RequestID = 7)
    UPDATE MaintenanceRequest SET ProfileID = 2, ApartmentID = 2, Title = N'Hỏng công tắc bình nóng lạnh', Description = N'Công tắc bật bình nóng lạnh phòng tắm chính bị lỏng lẻo gạt lên không sáng đèn và không nóng.', Status = 2, RequestDate = DATEADD(day, -4, GETDATE()) WHERE RequestID = 7;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceRequest ON;
    INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES (7, 2, 2, N'Hỏng công tắc bình nóng lạnh', N'Công tắc bật bình nóng lạnh phòng tắm chính bị lỏng lẻo gạt lên không sáng đèn và không nóng.', 2, DATEADD(day, -4, GETDATE()));
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

-- Task 2: assigned to Staff 5, Status 3 (Completed)
IF EXISTS (SELECT 1 FROM MaintenanceTask WHERE TaskID = 2)
    UPDATE MaintenanceTask SET RequestID = 3, StaffID = 5, AssignedBy = 2, AssignedDate = DATEADD(hour, -23, GETDATE()), Deadline = DATEADD(day, 2, GETDATE()), Status = 3 WHERE TaskID = 2;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceTask ON;
    INSERT INTO MaintenanceTask (TaskID, RequestID, StaffID, AssignedBy, AssignedDate, Deadline, Status) VALUES (2, 3, 5, 2, DATEADD(hour, -23, GETDATE()), DATEADD(day, 2, GETDATE()), 3);
    SET IDENTITY_INSERT MaintenanceTask OFF;
END

-- Task 3: assigned to Staff 4, Status 2 (In Progress)
IF EXISTS (SELECT 1 FROM MaintenanceTask WHERE TaskID = 3)
    UPDATE MaintenanceTask SET RequestID = 4, StaffID = 4, AssignedBy = 2, AssignedDate = DATEADD(day, -1, GETDATE()), Deadline = DATEADD(day, 1, GETDATE()), Status = 2 WHERE TaskID = 3;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceTask ON;
    INSERT INTO MaintenanceTask (TaskID, RequestID, StaffID, AssignedBy, AssignedDate, Deadline, Status) VALUES (3, 4, 4, 2, DATEADD(day, -1, GETDATE()), DATEADD(day, 1, GETDATE()), 2);
    SET IDENTITY_INSERT MaintenanceTask OFF;
END

-- Task 4: assigned to Staff 4, Status 3 (Completed)
IF EXISTS (SELECT 1 FROM MaintenanceTask WHERE TaskID = 4)
    UPDATE MaintenanceTask SET RequestID = 5, StaffID = 4, AssignedBy = 2, AssignedDate = DATEADD(day, -2, GETDATE()), Deadline = DATEADD(day, -1, GETDATE()), Status = 3 WHERE TaskID = 4;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceTask ON;
    INSERT INTO MaintenanceTask (TaskID, RequestID, StaffID, AssignedBy, AssignedDate, Deadline, Status) VALUES (4, 5, 4, 2, DATEADD(day, -2, GETDATE()), DATEADD(day, -1, GETDATE()), 3);
    SET IDENTITY_INSERT MaintenanceTask OFF;
END

-- Task 5: assigned to Staff 4, Status 1 (Assigned - Overdue!)
IF EXISTS (SELECT 1 FROM MaintenanceTask WHERE TaskID = 5)
    UPDATE MaintenanceTask SET RequestID = 6, StaffID = 4, AssignedBy = 2, AssignedDate = DATEADD(day, -3, GETDATE()), Deadline = DATEADD(day, -1, GETDATE()), Status = 1 WHERE TaskID = 5;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceTask ON;
    INSERT INTO MaintenanceTask (TaskID, RequestID, StaffID, AssignedBy, AssignedDate, Deadline, Status) VALUES (5, 6, 4, 2, DATEADD(day, -3, GETDATE()), DATEADD(day, -1, GETDATE()), 1);
    SET IDENTITY_INSERT MaintenanceTask OFF;
END

-- Task 6: assigned to Staff 4, Status 3 (Completed)
IF EXISTS (SELECT 1 FROM MaintenanceTask WHERE TaskID = 6)
    UPDATE MaintenanceTask SET RequestID = 7, StaffID = 4, AssignedBy = 2, AssignedDate = DATEADD(day, -4, GETDATE()), Deadline = DATEADD(day, -2, GETDATE()), Status = 3 WHERE TaskID = 6;
ELSE
BEGIN
    SET IDENTITY_INSERT MaintenanceTask ON;
    INSERT INTO MaintenanceTask (TaskID, RequestID, StaffID, AssignedBy, AssignedDate, Deadline, Status) VALUES (6, 7, 4, 2, DATEADD(day, -4, GETDATE()), DATEADD(day, -2, GETDATE()), 3);
    SET IDENTITY_INSERT MaintenanceTask OFF;
END
GO

-- 7. Maintenance Reports
SET IDENTITY_INSERT MaintenanceReport ON;

IF EXISTS (SELECT 1 FROM MaintenanceReport WHERE ReportID = 1)
    UPDATE MaintenanceReport SET TaskID = 3, ReportContent = N'Đã kiểm tra dàn nóng và lạnh. Block điều hòa không chạy do tụ đề bị hỏng. Đã liên hệ bên đại lý cung cấp linh kiện để thay tụ đề Panasonic mới.', ProgressPercent = 50, CreatedAt = DATEADD(hour, -12, GETDATE()) WHERE ReportID = 1;
ELSE
    INSERT INTO MaintenanceReport (ReportID, TaskID, ReportContent, ProgressPercent, CreatedAt) VALUES (1, 3, N'Đã kiểm tra dàn nóng và lạnh. Block điều hòa không chạy do tụ đề bị hỏng. Đã liên hệ bên đại lý cung cấp linh kiện để thay tụ đề Panasonic mới.', 50, DATEADD(hour, -12, GETDATE()));

IF EXISTS (SELECT 1 FROM MaintenanceReport WHERE ReportID = 2)
    UPDATE MaintenanceReport SET TaskID = 4, ReportContent = N'Tháo bỏ chấn lưu bóng đèn cũ bị hỏng và vệ sinh máng đèn LED trước khi lắp bóng mới.', ProgressPercent = 50, CreatedAt = DATEADD(day, -1, GETDATE()) WHERE ReportID = 2;
ELSE
    INSERT INTO MaintenanceReport (ReportID, TaskID, ReportContent, ProgressPercent, CreatedAt) VALUES (2, 4, N'Tháo bỏ chấn lưu bóng đèn cũ bị hỏng và vệ sinh máng đèn LED trước khi lắp bóng mới.', 50, DATEADD(day, -1, GETDATE()));

IF EXISTS (SELECT 1 FROM MaintenanceReport WHERE ReportID = 3)
    UPDATE MaintenanceReport SET TaskID = 4, ReportContent = N'Đã thay bóng LED Philips 9W mới. Ánh sáng đều tốt, công suất ổn định.', ProgressPercent = 100, CreatedAt = DATEADD(hour, -18, GETDATE()) WHERE ReportID = 3;
ELSE
    INSERT INTO MaintenanceReport (ReportID, TaskID, ReportContent, ProgressPercent, CreatedAt) VALUES (3, 4, N'Đã thay bóng LED Philips 9W mới. Ánh sáng đều tốt, công suất ổn định.', 100, DATEADD(hour, -18, GETDATE()));

IF EXISTS (SELECT 1 FROM MaintenanceReport WHERE ReportID = 4)
    UPDATE MaintenanceReport SET TaskID = 6, ReportContent = N'Đo kiểm tra thấy tiếp điểm công tắc bị chập cháy sun nhựa bên trong hạt nút ấn.', ProgressPercent = 30, CreatedAt = DATEADD(day, -3, GETDATE()) WHERE ReportID = 4;
ELSE
    INSERT INTO MaintenanceReport (ReportID, TaskID, ReportContent, ProgressPercent, CreatedAt) VALUES (4, 6, N'Đo kiểm tra thấy tiếp điểm công tắc bị chập cháy sun nhựa bên trong hạt nút ấn.', 30, DATEADD(day, -3, GETDATE()));

IF EXISTS (SELECT 1 FROM MaintenanceReport WHERE ReportID = 5)
    UPDATE MaintenanceReport SET TaskID = 6, ReportContent = N'Đã thay thế hạt công tắc Panasonic chính hãng mới. Bình nóng lạnh bật tắt hoạt động bình thường, an toàn.', ProgressPercent = 100, CreatedAt = DATEADD(day, -2, GETDATE()) WHERE ReportID = 5;
ELSE
    INSERT INTO MaintenanceReport (ReportID, TaskID, ReportContent, ProgressPercent, CreatedAt) VALUES (5, 6, N'Đã thay thế hạt công tắc Panasonic chính hãng mới. Bình nóng lạnh bật tắt hoạt động bình thường, an toàn.', 100, DATEADD(day, -2, GETDATE()));

SET IDENTITY_INSERT MaintenanceReport OFF;
GO
