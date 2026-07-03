-- ============================================================
-- MOCK DATA: Maintenance Request and related tables
-- Date: 2026-06-30
-- Description: Inserts test data to display maintenance requests on Manager portal
-- ============================================================

USE ABMS;
GO

-- Kích hoạt chèn khóa tự tăng để dễ quản lý khoá ngoại
SET IDENTITY_INSERT Role ON;
INSERT INTO Role (RoleID, RoleName) VALUES 
(1, 'ADMIN'), 
(2, 'MANAGER'), 
(3, 'RESIDENT'), 
(4, 'MAINTENANCE_STAFF');
SET IDENTITY_INSERT Role OFF;
GO

SET IDENTITY_INSERT Account ON;
INSERT INTO Account (AccountID, Username, Password, RoleID, Status) VALUES 
(1, 'admin1', '123', 1, 1),
(2, 'manager1', '123', 2, 1),
(3, 'resident1', '123', 3, 1),
(4, 'worker1', '123', 4, 1),
(5, 'worker2', '123', 4, 1);
SET IDENTITY_INSERT Account OFF;
GO

SET IDENTITY_INSERT Apartment ON;
INSERT INTO Apartment (ApartmentID, ApartmentNumber, Floor, Area, RoomType, Status) VALUES 
(1, 'A-101', 1, 75.50, '2 Bedrooms', 1),
(2, 'B-202', 2, 90.00, '3 Bedrooms', 1);
SET IDENTITY_INSERT Apartment OFF;
GO

SET IDENTITY_INSERT Profile ON;
INSERT INTO Profile (ProfileID, AccountID, ApartmentID, FullName, Gender, PhoneNumber, Email, CitizenID, IsHouseholdOwner, ResidentStatus) VALUES 
(1, 2, NULL, N'Nguyen Van Ly (Manager)', N'Nam', '0912345678', 'lynv@abms.com', '123456789001', 1, 1),
(2, 3, 1, N'Tran Duc Nam (Resident)', N'Nam', '0987654321', 'namtd@gmail.com', '123456789002', 1, 1),
(3, 4, NULL, N'Le Van Sua (Thợ ống nước)', N'Nam', '0922222222', 'sualv@abms.com', '123456789003', 0, 1),
(4, 5, NULL, N'Pham Minh Dien (Thợ sửa điện)', N'Nam', '0933333333', 'dienpm@abms.com', '123456789004', 0, 1);
SET IDENTITY_INSERT Profile OFF;
GO

SET IDENTITY_INSERT MaintenanceRequest ON;
INSERT INTO MaintenanceRequest (RequestID, ProfileID, ApartmentID, Title, Description, Status, RequestDate) VALUES 
-- Trạng thái: 0: PENDING | 1: ASSIGNED | 2: COMPLETED
(1, 2, 1, N'Rò rỉ đường ống nước phòng tắm', N'Vòi sen tắm nhà mình bị rò nước liên tục nhỏ giọt rất lãng phí, cần kỹ thuật viên hỗ trợ kiểm tra khóa van hoặc thay gioăng cao su mới giúp mình.', 0, DATEADD(hour, -5, GETDATE())), 
(2, 2, 1, N'Ổ cắm điện phòng khách bị cháy chập', N'Hôm qua nhà mình cắm bếp lẩu thì nghe tiếng nổ nhẹ và đánh lửa khét lẹt ở ổ cắm góc tivi, hiện tại ổ cắm đó đã bị cháy đen và không có điện nữa.', 1, DATEADD(hour, -2, GETDATE())), 
(3, 2, 1, N'Tay nắm chốt cửa ban công bị kẹt', N'Tay nắm cửa ban công hướng Đông bị kẹt cứng không thể gạt chốt khóa an toàn được, ban đêm gió lùa mạnh rất nguy hiểm.', 2, DATEADD(day, -1, GETDATE()));
SET IDENTITY_INSERT MaintenanceRequest OFF;
GO

SET IDENTITY_INSERT MaintenanceTask ON;
INSERT INTO MaintenanceTask (TaskID, RequestID, StaffID, AssignedBy, AssignedDate, Status) VALUES 
-- Task 1: Dành cho Request 2 (Trạng thái task: 1 = ASSIGNED / Incomplete - Nhân viên "worker1" Bận/Busy)
(1, 2, 4, 2, DATEADD(hour, -1, GETDATE()), 1),
-- Task 2: Dành cho Request 3 (Trạng thái task: 0 = COMPLETED / Nhân viên "worker2" Rảnh/Available)
(2, 3, 5, 2, DATEADD(hour, -23, GETDATE()), 0);
SET IDENTITY_INSERT MaintenanceTask OFF;
GO
