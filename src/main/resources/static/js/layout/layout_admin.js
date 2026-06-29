function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast-notification ${type}`;
    
    let iconClass = 'fa-circle-check';
    if (type === 'info') iconClass = 'fa-circle-info';
    
    toast.innerHTML = `
        <div class="toast-icon"><i class="fa-solid ${iconClass}"></i></div>
        <div class="toast-message">${message}</div>
    `;
    
    container.appendChild(toast);
    
    // Force reflow and add show class
    setTimeout(() => toast.classList.add('show'), 10);
    
    // Slide out and remove after 3 seconds
    setTimeout(() => {
        toast.classList.replace('show', 'hide');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

document.addEventListener('DOMContentLoaded', function() {
    const messageEl = document.getElementById('toastMessageData');
    if (messageEl) {
        const message = messageEl.getAttribute('data-message');
        const type = messageEl.getAttribute('data-type') || 'success';
        if (message) {
            showToast(message, type);
        }
    }
});
