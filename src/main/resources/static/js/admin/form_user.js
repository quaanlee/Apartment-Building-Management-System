document.addEventListener('DOMContentLoaded', function() {
    const tabs = document.querySelectorAll('.tab-btn');
    const panels = document.querySelectorAll('.tab-panel');

    function switchTab(tabId) {
        tabs.forEach(t => t.classList.remove('active'));
        panels.forEach(p => p.classList.remove('active'));

        const activeTabBtn = Array.from(tabs).find(t => t.getAttribute('data-tab') === tabId);
        if (activeTabBtn) {
            activeTabBtn.classList.add('active');
        }
        const activePanel = document.getElementById(tabId);
        if (activePanel) {
            activePanel.classList.add('active');
        }
    }

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const targetPanel = tab.getAttribute('data-tab');
            switchTab(targetPanel);
        });
    });

    // Keep tab active based on roleName from model if present
    const submittedRoleEl = document.getElementById('submittedRole');
    if (submittedRoleEl) {
        const submittedRole = submittedRoleEl.value ? submittedRoleEl.value.toUpperCase().replace(" ", "_") : "";
        if (submittedRole === 'MANAGER') {
            switchTab('manager-tab');
        } else if (submittedRole === 'MAINTENANCE_STAFF') {
            switchTab('staff-tab');
        } else if (submittedRole === 'ADMIN') {
            switchTab('admin-tab');
        } else {
            switchTab('resident-tab');
        }
    }
});
