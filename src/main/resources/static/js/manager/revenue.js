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
       2. BAR CHART — Revenue by Type
       ============================================================ */
    var barTypeEl = document.getElementById('barTypeData');
    var barTypeLabels = [];
    var barTypePcts = [];
    if (barTypeEl) {
        try {
            var lblStr = barTypeEl.getAttribute('data-labels') || '';
            var pctStr = barTypeEl.getAttribute('data-pcts') || '';
            barTypeLabels = lblStr ? lblStr.split(',') : [];
            barTypePcts = pctStr ? pctStr.split(',') : [];
        } catch(e) { barTypeLabels = []; barTypePcts = []; }
    }

    var barChart = document.getElementById('monthlyBarChart');
    var barLegendEl = document.getElementById('barTypeLegend');
    var barColors = {
        'RENT':'#451ebb', 'ELECTRICITY':'#d97706', 'WATER':'#2563eb',
        'PARKING':'#16a34a', 'SERVICE':'#be185d', 'PENALTY':'#b91c1c'
    };

    if (barChart && barTypeLabels.length > 0 && barTypeLabels.length === barTypePcts.length) {
        barChart.innerHTML = '';
        for (var bi = 0; bi < barTypeLabels.length; bi++) {
            var lbl = barTypeLabels[bi];
            var pct = parseInt(barTypePcts[bi]) || 0;
            var color = barColors[lbl] || '#64748b';

            var group = document.createElement('div');
            group.className = 'rev-bar-group';

            var bar = document.createElement('div');
            bar.className = 'rev-bar type-bar';
            bar.style.height = (pct / 100 * 130) + 'px';
            bar.style.background = color;
            bar.title = lbl + ': ' + pct + '%';

            var pctLabel = document.createElement('span');
            pctLabel.className = 'rev-bar-pct';
            pctLabel.textContent = pct + '%';
            
            var lblSpan = document.createElement('span');
            lblSpan.className = 'rev-bar-label';
            lblSpan.textContent = lbl;

            group.appendChild(bar);
            group.appendChild(pctLabel);
            group.appendChild(lblSpan);
            barChart.appendChild(group);
        }

        // Generate legend
        if (barLegendEl) {
            barLegendEl.innerHTML = '';
            for (var bi = 0; bi < barTypeLabels.length; bi++) {
                var lbl = barTypeLabels[bi];
                var color = barColors[lbl] || '#64748b';
                var dot = document.createElement('span');
                dot.className = 'rev-legend-dot';
                dot.style.background = color;
                var txt = document.createElement('span');
                txt.textContent = lbl;
                if (bi > 0) barLegendEl.appendChild(document.createTextNode(' '));
                barLegendEl.appendChild(dot);
                barLegendEl.appendChild(txt);
            }
        }
    } else if (barChart) {
        barChart.innerHTML = '<div style="text-align:center;padding:40px 0;color:var(--color-secondary);font-family:Inter,sans-serif;font-size:13px">No revenue data for selected filters</div>';
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

    // Skip donut if no data
    if (donutSvg && donutData.length > 0) {
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
    } else if (donutSvg) {
        // Show placeholder when no data
        var msg = document.createElement('div');
        msg.style.textAlign = 'center';
        msg.style.padding = '40px 0';
        msg.style.color = 'var(--color-secondary)';
        msg.style.fontFamily = 'Inter, sans-serif';
        msg.style.fontSize = '13px';
        msg.textContent = 'No revenue data for selected filters';
        donutSvg.parentNode.appendChild(msg);
        if (donutLegend) donutLegend.innerHTML = '<div style="text-align:center;color:var(--color-secondary);font-size:13px;padding:10px">No data</div>';
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
    } else if (donutSvg) {
        // Show placeholder when no data
        var msg = document.createElement('div');
        msg.style.textAlign = 'center';
        msg.style.padding = '40px 0';
        msg.style.color = 'var(--color-secondary)';
        msg.style.fontFamily = 'Inter, sans-serif';
        msg.style.fontSize = '13px';
        msg.textContent = 'No revenue data for selected filters';
        donutSvg.parentNode.appendChild(msg);
        if (donutLegend) donutLegend.innerHTML = '<div style="text-align:center;color:var(--color-secondary);font-size:13px;padding:10px">No data</div>';
    }

    var printBtn = document.getElementById('printReportBtn');
    if (printBtn) {
        printBtn.addEventListener('click', function () {
            window.print();
        });
    } else if (donutSvg) {
        // Show placeholder when no data
        var msg = document.createElement('div');
        msg.style.textAlign = 'center';
        msg.style.padding = '40px 0';
        msg.style.color = 'var(--color-secondary)';
        msg.style.fontFamily = 'Inter, sans-serif';
        msg.style.fontSize = '13px';
        msg.textContent = 'No revenue data for selected filters';
        donutSvg.parentNode.appendChild(msg);
        if (donutLegend) donutLegend.innerHTML = '<div style="text-align:center;color:var(--color-secondary);font-size:13px;padding:10px">No data</div>';
    }
});
