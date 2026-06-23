// Initialize Lucide Icons
lucide.createIcons();

// Password visibility toggle logic
const passwordField = document.getElementById('password');
const toggleButton = document.getElementById('password-toggle');
const toggleIcon = document.getElementById('toggle-icon');

toggleButton.addEventListener('click', () => {
    const isPassword = passwordField.type === 'password';

    // Toggle input type
    passwordField.type = isPassword ? 'text' : 'password';

    // Toggle icon attribute
    toggleIcon.setAttribute('data-lucide', isPassword ? 'eye-off' : 'eye');

    // Re-render the icon
    lucide.createIcons({
        attrs: {
            id: 'toggle-icon'
        },
        nameAttr: 'data-lucide',
        icons: {
            'eye': lucide.icons.eye,
            'eye-off': lucide.icons.eyeOff
        }
    });
});