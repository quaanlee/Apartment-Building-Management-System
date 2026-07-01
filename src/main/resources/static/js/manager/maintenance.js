/* ================================================================
   MAINTENANCE - JavaScript
   Features:
   - Calendar date pickers
   - Detail drawer (slide-in)
   - Create request modal
   - Approve/Reject actions
   - Task reports (AJAX)
   ================================================================ */

document.addEventListener('DOMContentLoaded', function () {

    /* ============================================================
       1. DETAIL DRAWER - Maintenance Requests
       ============================================================ */
    window.openReqDetail = function (row) {
        if (!row) return;
        var reqDetailOverlay = document.getElementById('reqDetailOverlay');
        var reqDetailPanel = document.getElementById('reqDetailPanel');
        if (!reqDetailOverlay || !reqDetailPanel) return;

        var id = row.getAttribute('data-id') || '';
        var resident = row.getAttribute('data-resident') || '';
        var apartment = row.getAttribute('data-apartment') || '';
        var title = row.getAttribute('data-title') || '';
        var desc = row.getAttribute('data-description') || '';
        var image = row.getAttribute('data-image') || '';
        var date = row.getAttribute('data-date') || '';
        var status = parseInt(row.getAttribute('data-status')) || 0;

        document.getElementById('reqDetailId').textContent = 'ID: #' + id;
        document.getElementById('reqDetailResident').textContent = resident;
        document.getElementById('reqDetailApartment').textContent = apartment;
        document.getElementById('reqDetailTitle').textContent = title;
        document.getElementById('reqDetailDesc').textContent = desc;
        document.getElementById('reqDetailDate').textContent = date;

        var statusMap = {0:'Pending', 1:'In Progress', 2:'Done', 3:'Rejected'};
        var statusEl = document.getElementById('reqDetailStatus');
        statusEl.textContent = statusMap[status] || 'Unknown';

        var imageWrap = document.getElementById('reqDetailImageWrap');
        var imageEl = document.getElementById('reqDetailImage');
        if (image) {
            imageWrap.style.display = 'block';
            imageEl.src = image;
        } else {
            imageWrap.style.display = 'none';
        }

        // Show/hide approve/reject buttons
        var actionsEl = document.getElementById('reqDetailActions');
        if (actionsEl) {
            var forms = actionsEl.querySelectorAll('form');
            var actionForms = [];
            forms.forEach(function(f) {
                var match = f.id.match(/^(approve|reject)Form$/);
                if (match) actionForms.push({el: f, type: match[1]});
            });

            if (status === 0) {
                actionForms.forEach(function(a) {
                    a.el.style.display = 'inline';
                    a.el.action = '/manager/maintenance/requests/' + id + '/' + a.type;
                });
                // Also rebuild action attribute for Thymeleaf
                var approveForm = document.getElementById('approveForm');
                var rejectForm = document.getElementById('rejectForm');
                if (approveForm) approveForm.setAttribute('action', '/manager/maintenance/requests/' + id + '/approve');
                if (rejectForm) rejectForm.setAttribute('action', '/manager/maintenance/requests/' + id + '/reject');
                actionsEl.style.display = 'flex';
            } else {
                actionsEl.style.display = 'none';
            }
        }

        reqDetailOverlay.classList.add('open');
        reqDetailPanel.classList.add('open');
        document.body.style.overflow = 'hidden';
    };

    window.closeReqDetail = function () {
        var panel = document.getElementById('reqDetailPanel');
        var overlay = document.getElementById('reqDetailOverlay');
        if (panel) panel.classList.remove('open');
        if (overlay) overlay.classList.remove('open');
        document.body.style.overflow = '';
    };

    /* ============================================================
       2. CREATE REQUEST MODAL
       ============================================================ */
    window.openCreateModal = function () {
        var overlay = document.getElementById('createModalOverlay');
        var modal = document.getElementById('createModal');
        if (overlay) overlay.classList.add('open');
        if (modal) modal.classList.add('open');
    };

    window.closeCreateModal = function () {
        var overlay = document.getElementById('createModalOverlay');
        var modal = document.getElementById('createModal');
        if (overlay) overlay.classList.remove('open');
        if (modal) modal.classList.remove('open');
    };

    window.previewImage = function (event) {
        var preview = document.getElementById('imagePreview');
        if (!preview || !event.target.files || !event.target.files[0]) return;
        preview.innerHTML = '';
        var img = document.createElement('img');
        img.src = URL.createObjectURL(event.target.files[0]);
        preview.appendChild(img);
    };

    /* ============================================================
       3. DETAIL DRAWER - Maintenance Status (Tasks)
       ============================================================ */
    window.openTaskDetail = function (row) {
        if (!row) return;
        var overlay = document.getElementById('taskDetailOverlay');
        var panel = document.getElementById('taskDetailPanel');
        if (!overlay || !panel) return;

        var taskId = row.getAttribute('data-taskid') || '';
        var requestId = row.getAttribute('data-requestid') || '';
        var title = row.getAttribute('data-title') || '';
        var desc = row.getAttribute('data-description') || '';
        var image = row.getAttribute('data-image') || '';
        var apartment = row.getAttribute('data-apartment') || '';
        var resident = row.getAttribute('data-resident') || '';
        var staff = row.getAttribute('data-staff') || '';
        var deadline = row.getAttribute('data-deadline') || '';
        var progress = row.getAttribute('data-progress') || '0';
        var status = parseInt(row.getAttribute('data-status')) || 0;

        document.getElementById('taskDetailId').textContent = 'Request ID: #' + requestId;
        document.getElementById('taskDetailTitle').textContent = title;
        document.getElementById('taskDetailDesc').textContent = desc;
        document.getElementById('taskDetailResident').textContent = resident;
        document.getElementById('taskDetailApt').textContent = apartment;
        document.getElementById('taskDetailStaff').textContent = staff || 'Unassigned';
        document.getElementById('taskDetailDeadline').textContent = deadline;

        var statusMap = {1:'Pending', 2:'In Progress', 3:'Done', 4:'Overdue'};
        document.getElementById('taskDetailStatus').textContent = statusMap[status] || 'Unknown';
        document.getElementById('taskDetailProgress').textContent = progress + '%';

        var imageWrap = document.getElementById('taskDetailImageWrap');
        var imageEl = document.getElementById('taskDetailImage');
        if (image) {
            imageWrap.style.display = 'block';
            imageEl.src = image;
        } else {
            imageWrap.style.display = 'none';
        }

        // Load reports via AJAX
        loadTaskReports(taskId);

        overlay.classList.add('open');
        panel.classList.add('open');
        document.body.style.overflow = 'hidden';
    };

    window.closeTaskDetail = function () {
        var panel = document.getElementById('taskDetailPanel');
        var overlay = document.getElementById('taskDetailOverlay');
        if (panel) panel.classList.remove('open');
        if (overlay) overlay.classList.remove('open');
        document.body.style.overflow = '';
    };

    function loadTaskReports(taskId) {
        var reportsList = document.getElementById('taskReportsList');
        if (!reportsList) return;
        reportsList.innerHTML = '<p style="color:var(--color-secondary);font-size:13px;">Loading...</p>';

        fetch('/manager/maintenance/tasks/' + taskId + '/reports')
            .then(function (r) { return r.json(); })
            .then(function (data) {
                if (!data || data.length === 0) {
                    reportsList.innerHTML = '<p style="color:var(--color-secondary);font-size:13px;">No reports yet.</p>';
                    return;
                }
                var html = '';
                data.forEach(function (report) {
                    var date = report.createdAt ? report.createdAt[0] + '-' +
                        String(report.createdAt[1]).padStart(2, '0') + '-' +
                        String(report.createdAt[2]).padStart(2, '0') + ' ' +
                        String(report.createdAt[3]).padStart(2, '0') + ':' +
                        String(report.createdAt[4]).padStart(2, '0') : '';
                    var content = report.reportContent || '';
                    var pct = report.progressPercent || 0;
                    html += '<div class="mt-report-item">' +
                        '<div class="mt-report-header">' +
                        '<span class="mt-report-date">' + date + '</span>' +
                        '<span class="mt-report-progress">' + pct + '%</span>' +
                        '</div>' +
                        '<p class="mt-report-content">' + content + '</p>' +
                        '</div>';
                });
                reportsList.innerHTML = html;
            })
            .catch(function () {
                reportsList.innerHTML = '<p style="color:var(--color-error);font-size:13px;">Failed to load reports.</p>';
            });
    }

    /* ============================================================
       4. ESC KEY
       ============================================================ */
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            closeReqDetail();
            closeCreateModal();
            closeTaskDetail();
        }
    });

});
