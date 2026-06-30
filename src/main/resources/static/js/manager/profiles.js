// Manager Profile Directory Interactions

document.addEventListener('DOMContentLoaded', function() {
    // Select Search input and set focus on end if it has value
    const searchInput = document.querySelector('.search-input');
    if (searchInput && searchInput.value) {
        searchInput.focus();
        const val = searchInput.value;
        searchInput.value = '';
        searchInput.value = val;
    }
});
