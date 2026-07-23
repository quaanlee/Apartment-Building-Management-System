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

document.addEventListener('DOMContentLoaded', function () {
    const messageEl = document.getElementById('toastMessageData');
    if (messageEl) {
        const message = messageEl.getAttribute('data-message');
        const type = messageEl.getAttribute('data-type') || 'success';
        if (message) {
            showToast(message, type);
        }
    }

    // Global search bar handling
    const searchInput = document.querySelector(".search-input");
    if (searchInput) {
        // Read search parameter from URL on load (in case user was redirected here with a query)
        const urlParams = new URLSearchParams(window.location.search);
        const searchQuery = urlParams.get('search');
        if (searchQuery) {
            searchInput.value = searchQuery;
        }

        searchInput.addEventListener("keypress", function (e) {
            if (e.key === "Enter") {
                const val = searchInput.value.trim();
                const targetPath = "/admin/apartments";
                if (window.location.pathname === targetPath || window.location.pathname === targetPath + "/") {
                    // Already on the list page, client-side filtering handles it instantly.
                    // We just update the URL without page reload for clean user experience.
                    const newUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + '?search=' + encodeURIComponent(val);
                    window.history.pushState({ path: newUrl }, '', newUrl);
                } else {
                    window.location.href = `${targetPath}?search=${encodeURIComponent(val)}`;
                }
            }
        });
    }
});
