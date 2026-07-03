/* ================================================================
   SYSTEM LOGS - JavaScript
   Features:
   - Custom date picker calendars (from/to)
   - Role-colored badges (auto on load)
   - Eye icon detail drawer (slide-in panel)
   ================================================================ */

document.addEventListener('DOMContentLoaded', function () {

    /* ============================================================
       1. CALENDAR PICKERS
       ============================================================ */

    const months = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ];

    /**
     * CalendarPicker — mini calendar widget
     * @param {Object} cfg
     */
    function CalendarPicker(cfg) {
        this.wrapperEl = document.getElementById(cfg.wrapperId);
        this.calEl     = document.getElementById(cfg.calId);
        this.displayEl = document.getElementById(cfg.displayId);
        this.hiddenEl  = document.getElementById(cfg.hiddenId);
        this.prevBtn   = document.getElementById(cfg.prevId);
        this.nextBtn   = document.getElementById(cfg.nextId);
        this.monthYearEl = document.getElementById(cfg.monthYearId);
        this.daysEl    = document.getElementById(cfg.daysId);
        this.cancelBtn = document.getElementById(cfg.cancelId);
        this.applyBtn  = document.getElementById(cfg.applyId);

        const today = new Date();
        // Parse existing value if present
        const existing = this.hiddenEl.value;
        if (existing) {
            const parts = existing.split('-');
            this.current = new Date(+parts[0], +parts[1]-1, +parts[2]);
            this.selected = new Date(+parts[0], +parts[1]-1, +parts[2]);
        } else {
            this.current  = new Date(today.getFullYear(), today.getMonth(), 1);
            this.selected = null;
        }

        this._init();
    }

    CalendarPicker.prototype._init = function () {
        var self = this;

        // Toggle calendar on wrapper click
        this.wrapperEl.addEventListener('click', function (e) {
            e.stopPropagation();
            var isOpen = self.calEl.classList.contains('open');
            closeAllCalendars();
            if (!isOpen) {
                self.calEl.classList.add('open');
                self.wrapperEl.classList.add('active');
                self._render();
            }
        });

        this.prevBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            self.current.setMonth(self.current.getMonth() - 1);
            self._render();
        });

        this.nextBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            self.current.setMonth(self.current.getMonth() + 1);
            self._render();
        });

        this.cancelBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            self.calEl.classList.remove('open');
            self.wrapperEl.classList.remove('active');
        });

        this.applyBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            if (self.selected) {
                var y = self.selected.getFullYear();
                var m = String(self.selected.getMonth() + 1).padStart(2, '0');
                var d = String(self.selected.getDate()).padStart(2, '0');
                var formatted = y + '-' + m + '-' + d;
                var display   = months[self.selected.getMonth()].slice(0,3) + ' ' + d + ', ' + y;
                self.hiddenEl.value  = formatted;
                self.displayEl.value = display;
            }
            self.calEl.classList.remove('open');
            self.wrapperEl.classList.remove('active');
        });

        // Stop click inside calendar from bubbling
        this.calEl.addEventListener('click', function (e) {
            e.stopPropagation();
        });

        this._render();
    };

    CalendarPicker.prototype._render = function () {
        var year  = this.current.getFullYear();
        var month = this.current.getMonth();
        this.monthYearEl.textContent = months[month] + ' ' + year;

        this.daysEl.innerHTML = '';

        var firstDay  = new Date(year, month, 1).getDay();
        var totalDays = new Date(year, month + 1, 0).getDate();
        var today = new Date();

        // Blank cells before start
        for (var i = 0; i < firstDay; i++) {
            var prev = new Date(year, month, 0 - (firstDay - 1 - i));
            var btn = this._makeDay(prev.getDate(), true, false, false);
            this.daysEl.appendChild(btn);
        }

        for (var d = 1; d <= totalDays; d++) {
            var date = new Date(year, month, d);
            var isToday = (
                d === today.getDate() &&
                month === today.getMonth() &&
                year === today.getFullYear()
            );
            var isSelected = this.selected && (
                d === this.selected.getDate() &&
                month === this.selected.getMonth() &&
                year === this.selected.getFullYear()
            );
            var btn = this._makeDay(d, false, isToday, isSelected);
            (function (day, self) {
                btn.addEventListener('click', function (e) {
                    e.stopPropagation();
                    self.selected = new Date(year, month, day);
                    self._render();
                });
            })(d, this);
            this.daysEl.appendChild(btn);
        }
    };

    CalendarPicker.prototype._makeDay = function (num, outside, isToday, isSelected) {
        var btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'cal-day';
        if (outside)    btn.classList.add('outside');
        if (isToday)    btn.classList.add('today');
        if (isSelected) btn.classList.add('selected');
        btn.textContent = num;
        if (outside) btn.disabled = true;
        return btn;
    };

    function closeAllCalendars() {
        document.querySelectorAll('.logs-calendar-dropdown').forEach(function (el) {
            el.classList.remove('open');
        });
        document.querySelectorAll('.logs-date-input-wrapper').forEach(function (el) {
            el.classList.remove('active');
        });
    }

    // Close calendars when clicking outside
    document.addEventListener('click', function () {
        closeAllCalendars();
    });

    // Init calendars
    new CalendarPicker({
        wrapperId:   'fromDateWrapper',
        calId:       'fromCalendar',
        displayId:   'fromDateDisplay',
        hiddenId:    'fromDate',
        prevId:      'fromPrev',
        nextId:      'fromNext',
        monthYearId: 'fromMonthYear',
        daysId:      'fromDays',
        cancelId:    'fromCancel',
        applyId:     'fromApply'
    });

    new CalendarPicker({
        wrapperId:   'toDateWrapper',
        calId:       'toCalendar',
        displayId:   'toDateDisplay',
        hiddenId:    'toDate',
        prevId:      'toPrev',
        nextId:      'toNext',
        monthYearId: 'toMonthYear',
        daysId:      'toDays',
        cancelId:    'toCancel',
        applyId:     'toApply'
    });

    /* ============================================================
       2. DETAIL DRAWER
       ============================================================ */

    var overlay = document.getElementById('logDetailOverlay');
    var panel   = document.getElementById('logDetailPanel');

    window.openLogDetail = function (row) {
        if (!row) return;

        var logId       = row.getAttribute('data-logid') || '';
        var logTime     = row.getAttribute('data-logtime') || '';
        var action      = row.getAttribute('data-action') || '';
        var role        = row.getAttribute('data-role') || '';
        var accountName = row.getAttribute('data-accountname') || '';
        var entityType  = row.getAttribute('data-entitytype') || '';
        var entityId    = row.getAttribute('data-entityid') || '';
        var ip          = row.getAttribute('data-ip') || '';
        var details     = row.getAttribute('data-details') || '{}';

        // Update modal content
        document.getElementById('detailLogId').textContent   = 'ID: ' + logId;
        document.getElementById('detailId').textContent      = logId;
        document.getElementById('detailTime').textContent    = logTime;
        document.getElementById('detailAction').textContent  = action;
        document.getElementById('detailRole').textContent    = role;
        document.getElementById('detailAccountName').textContent = accountName;
        document.getElementById('detailEntityType').textContent  = entityType;
        document.getElementById('detailEntityId').textContent    = entityId;
        document.getElementById('detailIp').textContent          = ip;

        // Pretty-print JSON trace
        try {
            var parsed = JSON.parse(details);
            var jsonTrace = {
                log_id: logId,
                actor: { name: accountName, role: role },
                action: action,
                meta: Object.assign({ entity_type: entityType, entity_id: entityId }, parsed),
                context: { ip: ip }
            };
            document.getElementById('detailJson').textContent =
                JSON.stringify(jsonTrace, null, 2);
        } catch (e) {
            document.getElementById('detailJson').textContent =
                '{\n  "log_id": "' + logId + '",\n  "action": "' + action + '"\n}';
        }

        // Open
        overlay.classList.add('open');
        panel.classList.add('open');
        document.body.style.overflow = 'hidden';
    };

    window.closeLogDetail = function () {
        panel.classList.remove('open');
        overlay.classList.remove('open');
        document.body.style.overflow = '';
    };

    // Close on Escape key
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') window.closeLogDetail();
    });

    /* ============================================================
       3. EXPORT BUTTON (optional CSV trigger)
       ============================================================ */
    var exportBtn = document.getElementById('exportBtn');
    if (exportBtn) {
        exportBtn.addEventListener('click', function () {
            // You can wire this to a real CSV export endpoint:
            // window.location.href = '/admin/logs/export?...params...';
            alert('Export Data — wire to /admin/logs/export endpoint');
        });
    }
});
