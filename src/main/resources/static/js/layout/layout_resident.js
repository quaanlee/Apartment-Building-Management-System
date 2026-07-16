// Resident Layout Javascript

function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let iconClass = 'fa-circle-check';
    if (type === 'error') iconClass = 'fa-circle-xmark';
    if (type === 'warning') iconClass = 'fa-triangle-exclamation';

    toast.innerHTML = `
        <i class="fa-solid ${iconClass}"></i>
        <div class="toast-message">${message}</div>
        <button class="toast-close"><i class="fa-solid fa-xmark"></i></button>
    `;

    // Close button click
    toast.querySelector('.toast-close').addEventListener('click', () => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), 300);
    });

    container.appendChild(toast);

    // Auto-remove after 3 seconds
    setTimeout(() => {
        if (toast.parentNode) {
            toast.classList.add('fade-out');
            setTimeout(() => toast.remove(), 300);
        }
    }, 3000);
}

document.addEventListener('DOMContentLoaded', function() {
    // 1. Toast Notification Check
    const messageEl = document.getElementById('toastMessageData');
    if (messageEl) {
        const message = messageEl.getAttribute('data-message');
        const type = messageEl.getAttribute('data-type') || 'success';
        if (message && message.trim() !== "") {
            showToast(message, type);
        }
    }

    // 2. Sidebar Toggle for Mobile
    const sidebarToggle = document.getElementById('sidebarToggle');
    const appContainer = document.querySelector('.app-container');
    if (sidebarToggle && appContainer) {
        sidebarToggle.addEventListener('click', function(e) {
            e.stopPropagation();
            appContainer.classList.toggle('sidebar-open');
        });

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', function(e) {
            if (appContainer.classList.contains('sidebar-open')) {
                const sidebar = document.querySelector('.sidebar');
                if (sidebar && !sidebar.contains(e.target) && e.target !== sidebarToggle) {
                    appContainer.classList.remove('sidebar-open');
                }
            }
        });
    }

    // 3. User Profile Dropdown Trigger
    const profileTrigger = document.getElementById('userProfileTrigger');
    const profileDropdown = document.getElementById('profileDropdown');
    if (profileTrigger && profileDropdown) {
        profileTrigger.addEventListener('click', function(e) {
            e.stopPropagation();
            profileDropdown.classList.toggle('show');
            const icon = profileTrigger.querySelector('.fa-chevron-down');
            if (icon) {
                icon.style.transform = profileDropdown.classList.contains('show') ? 'rotate(180deg)' : 'rotate(0deg)';
            }
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function() {
            if (profileDropdown.classList.contains('show')) {
                profileDropdown.classList.remove('show');
                const icon = profileTrigger.querySelector('.fa-chevron-down');
                if (icon) icon.style.transform = 'rotate(0deg)';
            }
        });
    }
});