/* ================================================================
   REVENUE REPORT - JavaScript
   Features:
   - Custom calendar date pickers (from/to)
   - Bar chart (monthly trend)
   - Donut chart (revenue by type)
   - Invoice detail drawer (slide-in)
   ================================================================ */

document.addEventListener('DOMContentLoaded', function () {

    /* ============================================================
       1. CALENDAR PICKER (Revenue page variant)
       ============================================================ */
    const months = ['January','February','March','April','May','June','July','August','September','October','November','December'];

    function RevCalendarPicker(cfg) {
        this.wrapperEl   = document.getElementById(cfg.wrapperId);
        this.calEl       = document.getElementById(cfg.calId);
        this.displayEl   = document.getElementById(cfg.displayId);
        this.hiddenEl    = document.getElementById(cfg.hiddenId);
        this.prevBtn     = document.getElementById(cfg.prevId);
        this.nextBtn     = document.getElementById(cfg.nextId);
        this.monthYearEl = document.getElementById(cfg.monthYearId);
        this.daysEl      = document.getElementById(cfg.daysId);
        this.cancelBtn   = document.getElementById(cfg.cancelId);
        this.applyBtn    = document.getElementById(cfg.applyId);

        const today = new Date();
        const existing = this.hiddenEl.value;
        if (existing) {
            const p = existing.split('-');
            this.current  = new Date(+p[0], +p[1]-1, 1);
            this.selected = new Date(+p[0], +p[1]-1, +p[2]);
        } else {
            this.current  = new Date(today.getFullYear(), today.getMonth(), 1);
            this.selected = null;
        }
        this._init();
    }

    RevCalendarPicker.prototype._init = function () {
        var self = this;

        this.wrapperEl.addEventListener('click', function (e) {
            e.stopPropagation();
            var isOpen = self.calEl.classList.contains('open');
            closeAllRevCals();
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
                self.hiddenEl.value  = y + '-' + m + '-' + d;
                self.displayEl.value = months[self.selected.getMonth()].slice(0,3) + ' ' + d + ', ' + y;
            }
            self.calEl.classList.remove('open');
            self.wrapperEl.classList.remove('active');
        });

        this.calEl.addEventListener('click', function (e) { e.stopPropagation(); });
        this._render();
    };

    RevCalendarPicker.prototype._render = function () {
        var year  = this.current.getFullYear();
        var month = this.current.getMonth();
        this.monthYearEl.textContent = months[month] + ' ' + year;
        this.daysEl.innerHTML = '';

        var firstDay  = new Date(year, month, 1).getDay();
        var totalDays = new Date(year, month + 1, 0).getDate();
        var today = new Date();

        for (var i = 0; i < firstDay; i++) {
            var btn = this._makeDay(new Date(year, month, 0 - (firstDay-1-i)).getDate(), true, false, false);
            this.daysEl.appendChild(btn);
        }

        for (var d = 1; d <= totalDays; d++) {
            var isToday = d === today.getDate() && month === today.getMonth() && year === today.getFullYear();
            var isSelected = this.selected &&
                d === this.selected.getDate() &&
                month === this.selected.getMonth() &&
                year === this.selected.getFullYear();
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

    RevCalendarPicker.prototype._makeDay = function (num, outside, isToday, isSelected) {
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

    function closeAllRevCals() {
        document.querySelectorAll('.rev-calendar-dropdown').forEach(function(el){ el.classList.remove('open'); });
        document.querySelectorAll('.rev-date-wrapper').forEach(function(el){ el.classList.remove('active'); });
    }

    document.addEventListener('click', function () { closeAllRevCals(); });

    new RevCalendarPicker({
        wrapperId: 'revFromWrapper', calId: 'revFromCal', displayId: 'revFromDisplay',
        hiddenId: 'revFromDate', prevId: 'revFromPrev', nextId: 'revFromNext',
        monthYearId: 'revFromMonthYear', daysId: 'revFromDays',
        cancelId: 'revFromCancel', applyId: 'revFromApply'
    });

    new RevCalendarPicker({
        wrapperId: 'revToWrapper', calId: 'revToCal', displayId: 'revToDisplay',
        hiddenId: 'revToDate', prevId: 'revToPrev', nextId: 'revToNext',
        monthYearId: 'revToMonthYear', daysId: 'revToDays',
        cancelId: 'revToCancel', applyId: 'revToApply'
    });

    /* ============================================================
       2. BAR CHART — Monthly Revenue Trend
       ============================================================ */
    var barDataEl = document.getElementById('barChartData');
    var barMonths = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    var monthlyData = [];
    if (barDataEl) {
        try {
            var cur = JSON.parse('[' + barDataEl.getAttribute('data-current').replace(/[\[\]]/g,'') + ']');
            var prv = JSON.parse('[' + barDataEl.getAttribute('data-prev').replace(/[\[\]]/g,'') + ']');
            for (var mi = 0; mi < 12; mi++) {
                monthlyData.push({
                    month: barMonths[mi],
                    current: cur[mi] ? Math.round(cur[mi] / 1000000) : 0,
                    prev: prv[mi] ? Math.round(prv[mi] / 1000000) : 0
                });
            }
        } catch(e) { monthlyData = []; }
    }

    var barChart = document.getElementById('monthlyBarChart');
    if (barChart) {
        var maxVal = Math.max.apply(null, monthlyData.map(function(d){ return Math.max(d.current, d.prev); }));
        monthlyData.forEach(function (d) {
            var group = document.createElement('div');
            group.className = 'rev-bar-group';

            var bCurrent = document.createElement('div');
            bCurrent.className = 'rev-bar current';
            bCurrent.style.height = (d.current / maxVal * 140) + 'px';
            bCurrent.title = 'Current: ' + d.current + 'M ₫';

            var bPrev = document.createElement('div');
            bPrev.className = 'rev-bar prev';
            bPrev.style.height = (d.prev / maxVal * 140) + 'px';
            bPrev.title = 'Previous: ' + d.prev + 'M ₫';

            var label = document.createElement('span');
            label.className = 'rev-bar-label';
            label.textContent = d.month;

            group.appendChild(bCurrent);
            group.appendChild(bPrev);
            group.appendChild(label);
            barChart.appendChild(group);
        });
    }

    /* ============================================================
       3. DONUT CHART — Revenue by Type
       ============================================================ */
    var donutColors = {
        'RENT':'#451ebb', 'ELECTRICITY':'#d97706', 'WATER':'#2563eb',
        'PARKING':'#16a34a', 'SERVICE':'#be185d', 'PENALTY':'#b91c1c'
    };
    var donutDataEl = document.getElementById('donutChartData');
    var donutData = [];
    if (donutDataEl) {
        var labels = (donutDataEl.getAttribute('data-labels') || '').split(',');
        var pcts = (donutDataEl.getAttribute('data-pcts') || '').split(',');
        for (var di = 0; di < labels.length && di < pcts.length; di++) {
            var lbl = labels[di].trim();
            var pct = parseInt(pcts[di]) || 0;
            if (lbl && pct > 0) {
                donutData.push({ label: lbl, pct: pct, color: donutColors[lbl] || '#64748b' });
            }
        }
    }

    var donutSvg    = document.getElementById('donutSvg');
    var donutLegend = document.getElementById('donutLegend');

    if (donutSvg) {
        var cx = 100, cy = 100, r = 70, stroke = 26;
        var circumference = 2 * Math.PI * r;
        var offset = 0;

        donutData.forEach(function (seg) {
            var dashLen = (seg.pct / 100) * circumference;
            var gap     = circumference - dashLen;

            var circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
            circle.setAttribute('cx', cx);
            circle.setAttribute('cy', cy);
            circle.setAttribute('r', r);
            circle.setAttribute('fill', 'none');
            circle.setAttribute('stroke', seg.color);
            circle.setAttribute('stroke-width', stroke);
            circle.setAttribute('stroke-dasharray', dashLen + ' ' + gap);
            circle.setAttribute('stroke-dashoffset', -offset * circumference / 100);
            circle.style.transition = 'stroke-dashoffset 0.4s ease';
            donutSvg.appendChild(circle);

            offset += seg.pct;

            // Legend item
            var item = document.createElement('div');
            item.className = 'rev-donut-legend-item';
            item.innerHTML =
                '<div class="rev-donut-legend-left">' +
                '<span class="rev-donut-dot" style="background:' + seg.color + '"></span>' +
                '<span>' + seg.label + '</span>' +
                '</div>' +
                '<span class="rev-donut-pct">' + seg.pct + '%</span>';
            donutLegend.appendChild(item);
        });
    }

    /* ============================================================
       4. DETAIL DRAWER
       ============================================================ */
    var revOverlay = document.getElementById('revDetailOverlay');
    var revPanel   = document.getElementById('revDetailPanel');

    window.openRevDetail = function (row) {
        if (!row) return;
        var invId    = row.getAttribute('data-invoiceid') || '';
        var resident = row.getAttribute('data-resident')  || '';
        var unit     = row.getAttribute('data-unit')      || '';
        var type     = row.getAttribute('data-type')      || '';
        var amount   = row.getAttribute('data-amount')    || '';
        var status   = row.getAttribute('data-status')    || '';
        var dueDate  = row.getAttribute('data-duedate')   || '';
        var paidDate = row.getAttribute('data-paiddate')  || '—';
        var note     = row.getAttribute('data-note')      || '';

        document.getElementById('revDetailId').textContent       = 'ID: ' + invId;
        document.getElementById('revDetailInvId').textContent    = invId;
        document.getElementById('revDetailAmount').textContent   = amount;
        document.getElementById('revDetailType').textContent     = type;
        document.getElementById('revDetailStatus').textContent   = status;
        document.getElementById('revDetailResident').textContent = resident;
        document.getElementById('revDetailUnit').textContent     = unit;
        document.getElementById('revDetailDue').textContent      = dueDate;
        document.getElementById('revDetailPaid').textContent     = paidDate || '—';
        document.getElementById('revDetailNote').textContent     = note || 'No additional notes.';

        revOverlay.classList.add('open');
        revPanel.classList.add('open');
        document.body.style.overflow = 'hidden';
    };

    window.closeRevDetail = function () {
        revPanel.classList.remove('open');
        revOverlay.classList.remove('open');
        document.body.style.overflow = '';
    };

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') window.closeRevDetail();
    });

    /* ============================================================
       5. EXPORT / PRINT BUTTONS
       ============================================================ */
    var exportBtn = document.getElementById('exportRevenueBtn');
    if (exportBtn) {
        exportBtn.addEventListener('click', function () {
            alert('Export Revenue Data — wire to /admin/revenue/export endpoint');
        });
    }

    var printBtn = document.getElementById('printReportBtn');
    if (printBtn) {
        printBtn.addEventListener('click', function () {
            window.print();
        });
    }
});
