const fromDateValue = document.getElementById("fromDateValue");
const toDateValue = document.getElementById("toDateValue");

const fromDateText = document.getElementById("fromDateText");
const toDateText = document.getElementById("toDateText");

const calendarPopup = document.getElementById("calendarPopup");
const calendarTitle = document.getElementById("calendarTitle");
const calendarDays = document.getElementById("calendarDays");
const calendarError = document.getElementById("calendarError");

const prevMonth = document.getElementById("prevMonth");
const nextMonth = document.getElementById("nextMonth");
const cancelDate = document.getElementById("cancelDate");
const applyDate = document.getElementById("applyDate");

const filterForm = document.getElementById("filterForm");

let currentPicker = null;
let viewDate = new Date(2023, 9, 1);
let selectedDate = null;

function parseDate(value) {
    if (!value) {
        return null;
    }

    const parts = value.split("-");
    return new Date(parts[0], parts[1] - 1, parts[2]);
}

function toInputValue(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}

function formatDate(date) {
    return date.toLocaleDateString("en-US", {
        month: "short",
        day: "numeric",
        year: "numeric"
    });
}

function isSameDate(a, b) {
    return a &&
        b &&
        a.getFullYear() === b.getFullYear() &&
        a.getMonth() === b.getMonth() &&
        a.getDate() === b.getDate();
}

function isBeforeOrEqual(a, b) {
    const dateA = new Date(a.getFullYear(), a.getMonth(), a.getDate());
    const dateB = new Date(b.getFullYear(), b.getMonth(), b.getDate());

    return dateA <= dateB;
}

function openCalendar(type) {
    currentPicker = type;

    if (type === "from") {
        selectedDate = parseDate(fromDateValue.value) || new Date();
        calendarPopup.style.left = "15px";
    } else {
        selectedDate = parseDate(toDateValue.value) || parseDate(fromDateValue.value) || new Date();
        calendarPopup.style.left = "231px";
    }

    viewDate = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), 1);

    calendarPopup.classList.remove("hidden");
    renderCalendar();
}

function renderCalendar() {
    calendarDays.innerHTML = "";
    calendarError.classList.add("hidden");

    const year = viewDate.getFullYear();
    const month = viewDate.getMonth();

    calendarTitle.textContent = viewDate.toLocaleDateString("en-US", {
        month: "long",
        year: "numeric"
    });

    const firstDayIndex = new Date(year, month, 1).getDay();
    const daysInCurrentMonth = new Date(year, month + 1, 0).getDate();
    const daysInPreviousMonth = new Date(year, month, 0).getDate();

    const days = [];

    for (let i = firstDayIndex - 1; i >= 0; i--) {
        const day = daysInPreviousMonth - i;

        days.push({
            day: day,
            date: new Date(year, month - 1, day),
            current: false
        });
    }

    for (let day = 1; day <= daysInCurrentMonth; day++) {
        days.push({
            day: day,
            date: new Date(year, month, day),
            current: true
        });
    }

    let nextDay = 1;

    while (days.length < 42) {
        days.push({
            day: nextDay,
            date: new Date(year, month + 1, nextDay),
            current: false
        });

        nextDay++;
    }

    days.forEach(item => {
        const button = document.createElement("button");

        button.type = "button";
        button.textContent = item.day;
        button.className = "calendar-day";

        if (!item.current) {
            button.classList.add("muted");
        }

        if (isSameDate(item.date, selectedDate)) {
            button.classList.add("selected");
        }

        if (currentPicker === "to") {
            const fromDate = parseDate(fromDateValue.value);

            if (fromDate && isBeforeOrEqual(item.date, fromDate)) {
                button.classList.add("disabled");
                button.disabled = true;
            }
        }

        button.addEventListener("click", function () {
            selectedDate = item.date;
            renderCalendar();
        });

        calendarDays.appendChild(button);
    });
}

document.querySelectorAll("[data-picker-target]").forEach(button => {
    button.addEventListener("click", function () {
        const type = this.getAttribute("data-picker-target");
        openCalendar(type);
    });
});

prevMonth.addEventListener("click", function () {
    viewDate = new Date(viewDate.getFullYear(), viewDate.getMonth() - 1, 1);
    renderCalendar();
});

nextMonth.addEventListener("click", function () {
    viewDate = new Date(viewDate.getFullYear(), viewDate.getMonth() + 1, 1);
    renderCalendar();
});

cancelDate.addEventListener("click", function () {
    calendarPopup.classList.add("hidden");
});

applyDate.addEventListener("click", function () {
    if (!selectedDate) {
        return;
    }

    if (currentPicker === "from") {
        fromDateValue.value = toInputValue(selectedDate);
        fromDateText.textContent = formatDate(selectedDate);

        const toDate = parseDate(toDateValue.value);

        if (toDate && isBeforeOrEqual(toDate, selectedDate)) {
            toDateValue.value = "";
            toDateText.textContent = "Select date";
        }

        calendarPopup.classList.add("hidden");
        return;
    }

    if (currentPicker === "to") {
        const fromDate = parseDate(fromDateValue.value);

        if (fromDate && isBeforeOrEqual(selectedDate, fromDate)) {
            calendarError.classList.remove("hidden");
            return;
        }

        toDateValue.value = toInputValue(selectedDate);
        toDateText.textContent = formatDate(selectedDate);

        calendarPopup.classList.add("hidden");
        filterForm.submit();
    }
});

document.addEventListener("click", function (event) {
    const isClickInsideCalendar = calendarPopup.contains(event.target);
    const isClickDateButton = event.target.closest("[data-picker-target]");

    if (!isClickInsideCalendar && !isClickDateButton) {
        calendarPopup.classList.add("hidden");
    }
});