/* ================================================================
   REVENUE REPORT - JavaScript
   Custom calendar date pickers (from/to)
   ================================================================ */

document.addEventListener('DOMContentLoaded', function () {
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
            if (this.selected && !isNaN(this.selected.getTime())) {
                const y = this.selected.getFullYear();
                const d = String(this.selected.getDate()).padStart(2, '0');
                this.displayEl.value = months[this.selected.getMonth()].slice(0,3) + ' ' + d + ', ' + y;
            }
        } else {
            this.current  = new Date(today.getFullYear(), today.getMonth(), 1);
            this.selected = null;
        }
        this.backupSelected = this.selected ? new Date(this.selected.getTime()) : null;
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
                self.backupSelected = self.selected ? new Date(self.selected.getTime()) : null;
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
            self.selected = self.backupSelected;
            if (self.selected) {
                var y = self.selected.getFullYear();
                var m = String(self.selected.getMonth() + 1).padStart(2, '0');
                var d = String(self.selected.getDate()).padStart(2, '0');
                self.hiddenEl.value  = y + '-' + m + '-' + d;
                self.displayEl.value = months[self.selected.getMonth()].slice(0,3) + ' ' + d + ', ' + y;
            } else {
                self.hiddenEl.value  = '';
                self.displayEl.value = '';
            }
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
            var d = new Date(year, month, 0 - (firstDay-1-i)).getDate();
            var btn = this._makeDay(d, true, false, false);
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

                    var y = self.selected.getFullYear();
                    var m = String(self.selected.getMonth() + 1).padStart(2, '0');
                    var dStr = String(self.selected.getDate()).padStart(2, '0');
                    self.hiddenEl.value  = y + '-' + m + '-' + dStr;
                    self.displayEl.value = months[self.selected.getMonth()].slice(0,3) + ' ' + dStr + ', ' + y;
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
});