// Profile Detail Subtabs Switching Script

document.addEventListener('DOMContentLoaded', function() {
    const subtabButtons = document.querySelectorAll('.subtab-btn');
    const subtabPanels = document.querySelectorAll('.subtab-panel');

    if (subtabButtons.length > 0) {
        subtabButtons.forEach(button => {
            button.addEventListener('click', function() {
                const targetPanelId = this.getAttribute('data-subtab');

                // 1. Deactivate all buttons
                subtabButtons.forEach(btn => btn.classList.remove('active'));
                
                // 2. Hide all panels
                subtabPanels.forEach(panel => panel.classList.remove('active'));

                // 3. Activate clicked button and target panel
                this.classList.add('active');
                const targetPanel = document.getElementById(targetPanelId);
                if (targetPanel) {
                    targetPanel.classList.add('active');
                }
            });
        });
    }
});
