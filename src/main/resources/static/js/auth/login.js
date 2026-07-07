document.addEventListener("DOMContentLoaded", function () {
    // Initialize Lucide icons
    if (typeof lucide !== "undefined") {
        lucide.createIcons();
    }

    // Password visibility toggle
    const passwordInput = document.getElementById("password");
    const toggleButton = document.getElementById("password-toggle");
    const toggleIcon = document.getElementById("toggle-icon");

    if (passwordInput && toggleButton && toggleIcon) {
        toggleButton.addEventListener("click", function () {
            const isPassword = passwordInput.getAttribute("type") === "password";
            passwordInput.setAttribute("type", isPassword ? "text" : "password");
            
            // Update icon
            toggleIcon.setAttribute("data-lucide", isPassword ? "eye-off" : "eye");
            if (typeof lucide !== "undefined") {
                lucide.createIcons();
            }
        });
    }
});
