# Maintenance Module — Manager Pages

## Overview

Hai trang quan ly bao tri danh cho role **Manager**, nam trong /manager/maintenance/:

1. **Maintenance Requests** (/manager/maintenance/requests) — Danh sach + tao yeu cau bao tri
2. **Maintenance Status** (/manager/maintenance/status) — Theo doi tien do xu ly

File structure:
`
src/main/java/com/quan/apartment_building_management_system/
├── controller/manager/
│   └── ManagerMaintenanceController.java
├── dto/
│   ├── MaintenanceRequestDTO.java
│   └── MaintenanceTaskDTO.java
├── repository/
│   ├── MaintenanceRequestRepository.java     (da co, bo sung query)
│   └── MaintenanceTaskRepository.java        (da co, bo sung query)
├── service/...                               (da co, khong can sua)

src/main/resources/
├── templates/manager/maintenance/
│   ├── requests.html
│   └── status.html
├── static/
│   ├── css/manager/maintenance.css
│   └── js/manager/maintenance.js
`

---

## 1. Maintenance Requests

### Entity
MaintenanceRequest
- equestId (Integer, PK)
- profile (ManyToOne → Profile)
- partment (ManyToOne → Apartment)
- 	itle (String)
- description (String)
- imageUrl (String)
- equestDate (LocalDateTime)
- status (Byte): 0=Pending, 1=InProgress, 2=Done, 3=Rejected
- maintenanceTask (OneToOne → MaintenanceTask)

### Backend

#### Repository (MaintenanceRequestRepository)
Bo sung cac method:
`java
List<MaintenanceRequest> findAllByOrderByRequestDateDesc();

@Query("SELECT r FROM MaintenanceRequest r WHERE "
    + "(:status IS NULL OR r.status = :status) "
    + "AND (:apartmentId IS NULL OR r.apartment.apartmentId = :apartmentId) "
    + "AND (:fromDate IS NULL OR r.requestDate >= :fromDate) "
    + "AND (:toDate IS NULL OR r.requestDate <= :toDate) "
    + "ORDER BY r.requestDate DESC")
List<MaintenanceRequest> findFiltered(
    @Param("status") Byte status,
    @Param("apartmentId") Integer apartmentId,
    @Param("fromDate") LocalDateTime fromDate,
    @Param("toDate") LocalDateTime toDate);
`

#### DTO (MaintenanceRequestDTO)
`java
class MaintenanceRequestDTO {
    Integer requestId;
    String residentName;
    String residentInitials;
    String apartmentNumber;
    String title;
    String description;
    String imageUrl;
    LocalDateTime requestDate;
    Byte status; // 0=Pending, 1=InProgress, 2=Done, 3=Rejected
    // Constructor + getters
}
`

#### Controller (ManagerMaintenanceController)
Mapping prefix: /manager/maintenance

| Method | Path | Chuc nang |
|--------|------|-----------|
| GET | /requests | List requests, co params filter: status, partmentId, romDate, 	oDate |
| POST | /requests/create | Tao request moi. Form params: partmentId, 	itle, description + image (MultipartFile). Luu MaintenanceRequest status=0. |
| POST | /requests/{id}/approve | Duyet request. Cap nhat status=1, tao MaintenanceTask (status=1) lien ket voi request. |
| POST | /requests/{id}/reject | Tu choi request. Cap nhat status=3. |

**Luong approve:**
1. Tim MaintenanceRequest theo id
2. Set status = 1 (InProgress)
3. Tao MaintenanceTask:
   - maintenanceRequest = request da duyet
   - staff = current user hoac null (cho phep gan sau)
   - ssignedBy = current user
   - ssignedDate = now
   - status = 1 (Pending assignment)
4. Save request + task

#### Xu ly upload image
- Dung MultipartFile, luu vao uploads/maintenance/ folder
- imageUrl = duong dan relative /uploads/maintenance/{filename}
- File name: eq_{requestId}_{timestamp}.{ext}
- Neu khong co image, imageUrl = null

### Frontend

#### HTML (equests.html)
Dung layout: layout/manager/layout_manager voi active tab 'maintenance-requests'

**Layout:**
`
[Page Header: "Maintenance Requests"]
[Filter Bar: Status | Apartment | From Date | To Date | Apply | Clear]
[Table: ID | Resident | Apartment | Title | Date | Status | Actions]
[Float button: "+ New Request"]
[Create Request Modal]
[Detail Drawer (slide-in)]
`

**Filter bar:**
- Status dropdown: All, Pending, InProgress, Done, Rejected
- Apartment dropdown: All, A-101, B-205, ...
- Date range From-To (custom calendar)

**Table columns:**
| Request ID | Resident | Apartment | Title | Request Date | Status | Actions |
|------------|----------|-----------|-------|-------------|--------|---------|
| #1 | Nguyen Van A | A-101 | Water leak | 2026-06-28 | Pending (badge) | View |

**Actions:** Eye icon → open detail drawer

**Status badges:**
- 0=Pending: yellow badge
- 1=InProgress: blue badge
- 2=Done: green badge
- 3=Rejected: red badge

**Create Request Modal:**
- Apartment: dropdown (lay tu DB)
- Title: text input
- Description: textarea
- Image: file upload (accept image)
- Submit button

**Detail Drawer:**
- Resident name
- Apartment number
- Title
- Description
- Image (neu co)
- Request date
- Status badge
- If status = Pending: Approve + Reject buttons (chi Manager/Admin)

#### JS (maintenance.js)
- Filter form submit → reload page voi params
- Calendar picker (date range)
- Open detail drawer (click row) → fetch data tu data attributes
- Approve/Reject buttons → POST fetch + reload
- Create request modal → form validation + submit
- Upload image preview

---

## 2. Maintenance Status

### Entity
MaintenanceTask
- 	askId (Integer, PK)
- maintenanceRequest (OneToOne → MaintenanceRequest)
- staff (ManyToOne → Account)
- ssignedBy (ManyToOne → Account)
- ssignedDate (LocalDateTime)
- deadline (LocalDateTime)
- status (Byte): 1=Pending, 2=InProgress, 3=Done, 4=Overdue
- maintenanceReports (OneToMany → MaintenanceReport)

MaintenanceReport
- eportId (Long, PK)
- maintenanceTask (ManyToOne → MaintenanceTask)
- eportContent (String)
- progressPercent (Byte)
- createdAt (LocalDateTime)

### Backend

#### Repository (MaintenanceTaskRepository)
Bo sung:
`java
List<MaintenanceTask> findAllByOrderByAssignedDateDesc();

@Query("SELECT t FROM MaintenanceTask t WHERE "
    + "(:status IS NULL OR t.status = :status) "
    + "AND (:staffId IS NULL OR t.staff.accountId = :staffId) "
    + "AND (:fromDate IS NULL OR t.assignedDate >= :fromDate) "
    + "AND (:toDate IS NULL OR t.assignedDate <= :toDate) "
    + "ORDER BY t.assignedDate DESC")
List<MaintenanceTask> findFiltered(
    @Param("status") Byte status,
    @Param("staffId") Integer staffId,
    @Param("fromDate") LocalDateTime fromDate,
    @Param("toDate") LocalDateTime toDate);
`

#### DTO (MaintenanceTaskDTO)
`java
class MaintenanceTaskDTO {
    Integer taskId;
    Integer requestId;
    String requestTitle;
    String requestDescription;
    String requestImageUrl;
    LocalDateTime requestDate;
    String apartmentNumber;
    String residentName;
    String staffName;
    String assignedByName;
    LocalDateTime assignedDate;
    LocalDateTime deadline;
    Byte status;
    Byte progressPercent; // từ report mới nhất
    List<MaintenanceReport> reports;
}
`

#### Controller (ManagerMaintenanceController)

| Method | Path | Chuc nang |
|--------|------|-----------|
| GET | /status | List tasks, filter params: status, staffId, romDate, 	oDate |
| GET | /tasks/{id}/reports | Lay danh sach MaintenanceReport cua 1 task (AJAX, tra ve JSON) |

#### Thong ke logic
Tinh toan tu list tasks:
- Tong task: 	asks.size()
- Dang xu ly: status == 1 or 2
- Hoan thanh: status == 3
- Qua han: status == 4 hoac deadline < now va chua hoan thanh

### Frontend

#### HTML (status.html)
Layout: layout/manager/layout_manager voi active tab 'maintenance-status'

**Layout:**
`
[Page Header: "Maintenance Status"]
[KPI Cards: Total Tasks | In Progress | Completed | Overdue]
[Filter Bar: Status | Staff | From Date | To Date | Apply | Clear]
[Table: ID | Title | Staff | Deadline | Status | Progress]
[Detail Drawer (slide-in)]
`

**KPI Cards (4 cards):**
- Total Tasks — tong so
- In Progress — so task dang xu ly
- Completed — so task hoan thanh
- Overdue — so task qua han

**Filter bar:**
- Status dropdown: All, Pending, InProgress, Done, Overdue
- Staff dropdown: All, Alex Mercer, Jane Doe, ...
- Date range From-To

**Table columns:**
| Request ID | Title | Staff | Deadline | Status | Progress |
|------------|-------|-------|----------|--------|----------|
| #1 | Water leak | Nguyen Van A | 2026-07-05 | InProgress (badge) | [progress bar 60%] |

**Status badges:**
- 1=Pending: gray
- 2=InProgress: blue
- 3=Done: green
- 4=Overdue: red

**Progress bar:**
- <div class="progress-bar"> CSS, width = progressPercent%
- Color: xanh la neu >= 80, vang neu 50-79, do neu < 50

**Detail Drawer:**
- Request info: Title, Description, Image
- Task info: Staff, AssignedDate, Deadline, Status
- Tab "Reports" (AJAX): list reports theo thoi gian
  - Moi report: CreatedAt, ProgressPercent, ReportContent

#### JS (maintenance.js)
- Filter form submit → reload
- KPI tinh tu server-side data
- Open detail drawer → load request + task data tu data attributes
- Click "Reports" tab → AJAX fetch /manager/maintenance/tasks/{id}/reports → render list
- Calendar picker

---

## 3. Layout & Navigation

Them 2 tab vao layout_manager.html:
- "Maintenance Requests" → /manager/maintenance/requests
- "Maintenance Status" → /manager/maintenance/status

Active tab xac dinh bang bien ctiveTab truyen tu controller.

---

## 4. DataInitializer (seed data)

Them 5-10 MaintenanceRequest vao DataInitializer.java:
- Resident: dung Profile co san
- Apartment: dung Apartment co san
- Status: 0, 1, 2, 3 (phân bo deu)
- MaintenanceTask cho request co status = 1 hoac 2
- 1-2 MaintenanceReport cho moi task
