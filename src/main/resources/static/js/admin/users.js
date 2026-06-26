document.addEventListener('DOMContentLoaded', function() {
    const headerSearch = document.querySelector('.search-input');
    if (headerSearch) {
        // Pre-populate search text from parameter
        const urlParams = new URLSearchParams(window.location.search);
        const searchVal = urlParams.get('search');
        if (searchVal) {
            headerSearch.value = searchVal;
        }
        
        // Submit filter on Enter press
        headerSearch.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                const form = document.querySelector('.filter-form');
                let hiddenSearch = form.querySelector('input[name="search"]');
                if (!hiddenSearch) {
                    hiddenSearch = document.createElement('input');
                    hiddenSearch.type = 'hidden';
                    hiddenSearch.name = 'search';
                    form.appendChild(hiddenSearch);
                }
                hiddenSearch.value = headerSearch.value;
                form.submit();
            }
        });
    }
});
