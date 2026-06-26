document.addEventListener('DOMContentLoaded', function () {
    // 1. Toggling utility drawers (expansion panels)
    const dropdownBtns = document.querySelectorAll('.dropdown-btn');
    dropdownBtns.forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            const cardRow = this.closest('.card-row');
            const isExpanded = cardRow.classList.contains('expanded');

            // Close all other expanded rows first
            document.querySelectorAll('.card-row.expanded').forEach(row => {
                if (row !== cardRow) {
                    row.classList.remove('expanded');
                    const otherBtn = row.querySelector('.dropdown-btn');
                    if (otherBtn) {
                        otherBtn.innerHTML = `Others <i class="fas fa-chevron-down"></i>`;
                    }
                }
            });

            // Toggle current row
            if (isExpanded) {
                cardRow.classList.remove('expanded');
                this.innerHTML = `Others <i class="fas fa-chevron-down"></i>`;
            } else {
                cardRow.classList.add('expanded');
                this.innerHTML = `Others <i class="fas fa-chevron-up"></i>`;
            }
        });
    });

    // 2. Close modals when clicking close button or overlay
    const modals = document.querySelectorAll('.modal-overlay');
    modals.forEach(modal => {
        const closeBtn = modal.querySelector('.modal-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                modal.classList.remove('open');
            });
        }
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.remove('open');
            }
        });
    });

    // Auto-close toast message after 4 seconds
    const toastAlert = document.getElementById('toastAlert');
    if (toastAlert) {
        setTimeout(() => {
            closeToast();
        }, 4000);
    }

    // 3. Enable disabled dropdowns in pricingModal on form submit so values are posted
    const pricingForm = document.querySelector('#pricingModal form');
    if (pricingForm) {
        pricingForm.addEventListener('submit', function () {
            document.getElementById('pricingUtilityId').disabled = false;
            document.getElementById('pricingUnitId').disabled = false;
        });
    }
});

// Helper functions for modals
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('open');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('open');
    }
}

// Open modal to add a new utility
function openAddUtilityModal() {
    document.getElementById('utilityModalTitle').innerText = 'Add New Utility';
    document.getElementById('utilityIdField').value = '';
    document.getElementById('utilityNameField').value = '';
    document.getElementById('utilityDescField').value = '';
    document.getElementById('utilityStatusField').value = 'true';
    openModal('utilityModal');
}

// Open modal to edit a utility
function openEditUtilityModal(id, name, description, status) {
    document.getElementById('utilityModalTitle').innerText = 'Edit Utility';
    document.getElementById('utilityIdField').value = id;
    document.getElementById('utilityNameField').value = name;
    document.getElementById('utilityDescField').value = description;
    document.getElementById('utilityStatusField').value = status.toString();
    openModal('utilityModal');
}

// Open modal to add resource
function openAddResourceModal(utilityId, utilityName) {
    document.getElementById('resourceUtilityId').value = utilityId;
    document.getElementById('resourceTitle').innerText = `Add Resource to ${utilityName}`;
    document.getElementById('resourceName').value = '';
    document.getElementById('resourceLocation').value = '';
    document.getElementById('resourceStatus').value = 'true';
    openModal('resourceModal');
}

// Open modal to manage pricing
function openManagePricingModal(utilityId, utilityName, currentUnitId, currentPrice) {
    document.getElementById('pricingUtilityId').value = utilityId;
    document.getElementById('pricingTitle').innerText = `Manage Pricing for ${utilityName}`;

    const unitSelect = document.getElementById('pricingUnitId');
    if (currentUnitId) {
        unitSelect.value = currentUnitId;
    } else {
        unitSelect.selectedIndex = 0;
    }

    const priceInput = document.getElementById('pricingPrice');
    if (currentPrice) {
        priceInput.value = parseFloat(currentPrice).toFixed(2);
    } else {
        priceInput.value = '0.00';
    }

    openModal('pricingModal');
}

// Open modal to configure pricing for any utility
function openConfigurePricingModal() {
    document.getElementById('pricingTitle').innerText = 'Configure Pricing';
    
    const utilSelect = document.getElementById('pricingUtilityId');
    utilSelect.disabled = false;
    if (utilSelect.options.length > 0) {
        utilSelect.selectedIndex = 0;
    }
    
    const unitSelect = document.getElementById('pricingUnitId');
    unitSelect.disabled = false;
    unitSelect.selectedIndex = 0;
    
    document.getElementById('pricingPrice').value = '0.00';
    openModal('pricingModal');
}

// Open modal to edit existing pricing
function openEditPricingModal(utilityId, unitId, price) {
    document.getElementById('pricingTitle').innerText = 'Edit Pricing';
    
    const utilSelect = document.getElementById('pricingUtilityId');
    utilSelect.value = utilityId;
    utilSelect.disabled = true;
    
    const unitSelect = document.getElementById('pricingUnitId');
    unitSelect.value = unitId;
    unitSelect.disabled = true;
    
    document.getElementById('pricingPrice').value = parseFloat(price).toFixed(2);
    openModal('pricingModal');
}

// Function to close toast notification with fade-out effect
function closeToast() {
    const toastAlert = document.getElementById('toastAlert');
    if (toastAlert) {
        toastAlert.classList.add('fade-out');
        setTimeout(() => {
            toastAlert.remove();
        }, 300);
    }
}
