/**
 * utility_bookings.js
 * Client-side logic for the Manager Utility Booking Management page.
 *
 * Responsibilities:
 *  - Intercept filter/search/pagination actions and submit the #ubFilterForm to the server
 *  - Date picker open/close logic
 *  - View modal: load booking detail via AJAX, render, show
 *  - Confirm modal: approve / reject / cancel (revert to pending) with AJAX POST
 *  - Page reload after status change to refresh server-side rendered table & stats
 */

(function () {
  'use strict';

  // ── Constants ───────────────────────────────────────────────────────────────
  const BASE_URL    = '/manager/utility-bookings';

  // ── Status helpers ──────────────────────────────────────────────────────────
  const STATUS = { PENDING: 0, APPROVED: 1, REJECTED: 2, CANCELLED: 3 };

  const STATUS_LABEL = {
    0: 'Pending',
    1: 'Approved',
    2: 'Rejected',
    3: 'Cancelled',
  };

  const STATUS_CLASS = {
    0: 'ub-status-pending',
    1: 'ub-status-approved',
    2: 'ub-status-rejected',
    3: 'ub-status-cancelled',
  };

  // ── DOM refs ────────────────────────────────────────────────────────────────
  const filterForm         = document.getElementById('ubFilterForm');
  const pageInput          = document.getElementById('ubFormPage');
  const searchInput        = document.getElementById('ubSearch');
  const selectBookingStatus = document.getElementById('ubBookingStatus');
  const selectPaymentStatus = document.getElementById('ubPaymentStatus');
  const selectUtility       = document.getElementById('ubUtility');
  const paginationWrap      = document.getElementById('ubPagination');

  // Date pickers
  const startTimePicker = createDatePickerState('ubStartTimePicker', 'ubStartTimeFrom', 'ubStartTimeTo', 'ubStartTimeBtn', 'ubStartTimeLabel');
  const createdAtPicker = createDatePickerState('ubCreatedAtPicker', 'ubCreatedAtFrom', 'ubCreatedAtTo', 'ubCreatedAtBtn', 'ubCreatedAtLabel');

  // Modals
  const detailOverlay   = document.getElementById('ubDetailOverlay');
  const confirmOverlay  = document.getElementById('ubConfirmOverlay');

  // ── State ───────────────────────────────────────────────────────────────────
  let pendingAction   = null;   // { bookingId, newStatus }

  // ── Initialise ──────────────────────────────────────────────────────────────
  function init() {
    bindFilterEvents();
    bindDatePickerEvents(startTimePicker);
    bindDatePickerEvents(createdAtPicker);
    bindModalClose();
    bindConfirmModal();
    bindTableActions();
    bindPaginationEvents();
  }

  // ── Submit Helper ───────────────────────────────────────────────────────────
  function triggerFormSubmit(resetPage = true) {
    if (resetPage && pageInput) {
      pageInput.value = '0';
    }
    if (filterForm) {
      filterForm.submit();
    }
  }

  // ── Filter events ────────────────────────────────────────────────────────────
  function bindFilterEvents() {
    // Submit form when search changes (after user presses Enter)
    if (searchInput) {
      searchInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
          e.preventDefault();
          triggerFormSubmit(true);
        }
      });
    }

    if (selectBookingStatus) {
      selectBookingStatus.addEventListener('change', () => triggerFormSubmit(true));
    }
    if (selectPaymentStatus) {
      selectPaymentStatus.addEventListener('change', () => triggerFormSubmit(true));
    }
    if (selectUtility) {
      selectUtility.addEventListener('change', () => triggerFormSubmit(true));
    }

    const clearBtn = document.getElementById('ubBtnClearFilters');
    if (clearBtn) {
      clearBtn.addEventListener('click', () => {
        window.location.href = BASE_URL;
      });
    }
  }

  // ── Date picker factory ─────────────────────────────────────────────────────
  function createDatePickerState(pickerId, fromId, toId, btnId, labelId) {
    return { pickerId, fromId, toId, btnId, labelId };
  }

  function bindDatePickerEvents(picker) {
    const btn   = document.getElementById(picker.btnId);
    const panel = document.getElementById(picker.pickerId);
    if (!btn || !panel) return;
    
    const apply = panel.querySelector('.ub-date-apply');
    const cancel = panel.querySelector('.ub-date-cancel');

    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      closeAllDatePickers(picker.pickerId);
      panel.classList.toggle('open');
      btn.classList.toggle('active', panel.classList.contains('open'));
    });

    apply.addEventListener('click', () => {
      panel.classList.remove('open');
      btn.classList.remove('active');
      updateDateBtnLabel(picker);
      triggerFormSubmit(true);
    });

    cancel.addEventListener('click', () => {
      document.getElementById(picker.fromId).value = '';
      document.getElementById(picker.toId).value   = '';
      panel.classList.remove('open');
      btn.classList.remove('active');
      updateDateBtnLabel(picker);
      triggerFormSubmit(true);
    });
  }

  function updateDateBtnLabel(picker) {
    const from  = document.getElementById(picker.fromId).value;
    const to    = document.getElementById(picker.toId).value;
    const label = document.getElementById(picker.labelId);
    if (!label) return;
    if (from || to) {
      label.textContent = (from || '…') + ' – ' + (to || '…');
    } else {
      label.textContent = label.dataset.default;
    }
  }

  function closeAllDatePickers(exceptId) {
    document.querySelectorAll('.ub-date-picker').forEach(p => {
      if (p.id !== exceptId) p.classList.remove('open');
    });
    document.querySelectorAll('.ub-date-btn').forEach(b => {
      if (b.dataset.for !== exceptId) b.classList.remove('active');
    });
  }

  document.addEventListener('click', () => {
    closeAllDatePickers(null);
    document.querySelectorAll('.ub-date-btn').forEach(b => b.classList.remove('active'));
  });

  document.querySelectorAll('.ub-date-picker').forEach(p => {
    p.addEventListener('click', e => e.stopPropagation());
  });

  // ── Table action binding ─────────────────────────────────────────────────────
  function bindTableActions() {
    const tableBody = document.getElementById('ubTableBody');
    if (!tableBody) return;

    tableBody.querySelectorAll('[data-action]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id     = parseInt(btn.dataset.id, 10);
        const action = btn.dataset.action;
        if (action === 'view')    openDetailModal(id);
        if (action === 'approve') openConfirmModal(id, STATUS.APPROVED);
        if (action === 'reject')  openConfirmModal(id, STATUS.REJECTED);
        if (action === 'cancel')  openConfirmModal(id, STATUS.PENDING); // Cancel reverts status to Pending (0)
      });
    });
  }

  // ── Pagination events ────────────────────────────────────────────────────────
  function bindPaginationEvents() {
    if (!paginationWrap) return;
    paginationWrap.querySelectorAll('button[data-page]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        if (btn.disabled) return;
        const page = btn.dataset.page;
        if (pageInput) {
          pageInput.value = page;
          triggerFormSubmit(false); // Do not reset page index to 0
        }
      });
    });
  }

  // ── Detail Modal ─────────────────────────────────────────────────────────────
  function openDetailModal(bookingId) {
    const modal = detailOverlay.querySelector('.ub-modal');
    modal.innerHTML = `<div style="padding:40px;text-align:center"><div class="ub-spinner" style="margin:auto"></div></div>`;
    detailOverlay.classList.add('open');

    fetch(`${BASE_URL}/${bookingId}/detail`)
      .then(r => r.json())
      .then(resp => {
        if (!resp.success) { modal.innerHTML = `<p style="padding:24px;color:red">${esc(resp.message)}</p>`; return; }
        modal.innerHTML = buildDetailModalContent(resp.data);
        bindDetailModalActions(resp.data);
      })
      .catch(() => { modal.innerHTML = '<p style="padding:24px;color:red">Failed to load booking details.</p>'; });
  }

  function buildDetailModalContent(d) {
    const statusLabel = STATUS_LABEL[d.bookingStatus] || 'Unknown';
    const statusClass = STATUS_CLASS[d.bookingStatus] || '';
    const payClass    = d.paymentStatus === 'Paid' ? 'ub-payment-paid' : 'ub-payment-unpaid';
    const footerBtns  = buildDetailFooterButtons(d);

    return `
      <div class="ub-detail-header">
        <div class="ub-detail-title-row">
          <div class="ub-detail-icon"><i class="fas fa-calendar-check"></i></div>
          <div>
            <div class="ub-detail-title">Booking Details – #BB${String(d.bookingId).padStart(3,'0')}</div>
          </div>
        </div>
        <div style="display:flex;align-items:center;gap:10px">
          <span class="ub-status-badge ${statusClass}" style="font-size:14px">${statusLabel}</span>
          <button type="button" class="ub-detail-close" id="ubDetailClose"><i class="fas fa-times"></i></button>
        </div>
      </div>

      <div class="ub-detail-body">
        <!-- Resident Information -->
        <div class="ub-detail-section">
          <div class="ub-detail-section-title"><i class="fas fa-user"></i> Resident Information</div>
          <div class="ub-detail-info-grid">
            <div class="ub-detail-field"><label>Full Name</label><span>${esc(d.residentFullName || '—')}</span></div>
            <div class="ub-detail-field"><label>Phone</label><span>${esc(d.residentPhone || '—')}</span></div>
            <div class="ub-detail-field"><label>Email</label><span>${esc(d.residentEmail || '—')}</span></div>
          </div>
        </div>

        <!-- Booking Information -->
        <div class="ub-detail-section">
          <div class="ub-detail-section-title"><i class="fas fa-calendar-alt"></i> Booking Information</div>
          <div class="ub-detail-info-grid">
            <div class="ub-detail-field"><label>Utility</label><span>${esc(d.utilityName || '—')}</span></div>
            <div class="ub-detail-field"><label>Location</label><span>${esc(d.resourceLocation || d.resourceName || '—')}</span></div>
            <div class="ub-detail-field"><label>Start Time</label><span>${esc(d.startTime || '—')}</span></div>
            <div class="ub-detail-field"><label>Duration</label><span>${d.durationHours} ${d.durationHours === 1 ? 'Hour' : 'Hours'}</span></div>
            <div class="ub-detail-field"><label>Created At</label><span>${esc(d.createdAt || '—')}</span></div>
          </div>
        </div>

        <!-- Payment Details -->
        <div class="ub-detail-section">
          <div class="ub-detail-section-title"><i class="fas fa-credit-card"></i> Payment Details</div>
          <div class="ub-detail-info-grid">
            <div class="ub-detail-field"><label>Status</label>
              <span><span class="ub-payment-pill ${payClass}">${esc(d.paymentStatus)}</span></span>
            </div>
            <div class="ub-detail-field"><label>Amount</label><span>${d.amount != null ? '$' + parseFloat(d.amount).toFixed(2) : '—'}</span></div>
            <div class="ub-detail-field"><label>Transaction ID</label><span>${esc(d.transactionCode || '—')}</span></div>
            <div class="ub-detail-field"><label>Approved By</label><span>${esc(d.approvedByName || '—')}</span></div>
          </div>
        </div>
      </div>

      <div class="ub-detail-footer">
        ${footerBtns}
      </div>`;
  }

  function buildDetailFooterButtons(d) {
    const id = d.bookingId;
    const s = d.bookingStatus != null ? parseInt(d.bookingStatus, 10) : 0;
    if (s === STATUS.PENDING) {
      return `
        <button type="button" class="ub-btn ub-btn-detail-approve" data-action="approve" data-id="${id}">
          <i class="fas fa-check" style="margin-right:6px"></i>Approve
        </button>
        <button type="button" class="ub-btn ub-btn-detail-reject" data-action="reject" data-id="${id}">
          <i class="fas fa-times" style="margin-right:6px"></i>Reject
        </button>`;
    }
    if (s === STATUS.APPROVED) {
      return `<button type="button" class="ub-btn ub-btn-detail-cancel" data-action="cancel" data-id="${id}">
                <i class="fas fa-ban"></i> Cancel Approval
              </button>`;
    }
    if (s === STATUS.REJECTED) {
      return `<button type="button" class="ub-btn ub-btn-detail-cancel" data-action="cancel" data-id="${id}">
                <i class="fas fa-ban"></i> Cancel Rejection
              </button>`;
    }
    return ''; // Cancelled: no buttons
  }

  function bindDetailModalActions(data) {
    const modal = detailOverlay.querySelector('.ub-modal');
    if (!modal) return;

    const closeBtn = modal.querySelector('#ubDetailClose');
    if (closeBtn) closeBtn.addEventListener('click', () => closeDetailModal());

    modal.querySelectorAll('[data-action]').forEach(btn => {
      btn.addEventListener('click', () => {
        const id     = parseInt(btn.dataset.id, 10);
        const action = btn.dataset.action;
        closeDetailModal();
        if (action === 'approve') openConfirmModal(id, STATUS.APPROVED);
        if (action === 'reject')  openConfirmModal(id, STATUS.REJECTED);
        if (action === 'cancel')  openConfirmModal(id, STATUS.PENDING); // Cancel reverts status to Pending (0)
      });
    });
  }

  function closeDetailModal() {
    detailOverlay.classList.remove('open');
  }

  // ── Confirm Modal ─────────────────────────────────────────────────────────────
  function openConfirmModal(bookingId, newStatus) {
    pendingAction = { bookingId, newStatus };

    // Fetch data to fill confirm card
    fetch(`${BASE_URL}/${bookingId}/detail`)
      .then(r => r.json())
      .then(resp => {
        if (!resp.success) return;
        renderConfirmModal(resp.data, newStatus);
        confirmOverlay.classList.add('open');
      });
  }

  function renderConfirmModal(d, newStatus) {
    const modal = confirmOverlay.querySelector('.ub-modal');
    if (!modal) return;
    const config = getConfirmConfig(newStatus, d.bookingStatus);

    modal.innerHTML = `
      <div class="ub-confirm-body">
        <div class="ub-confirm-tag">Review Action</div>
        <div class="ub-confirm-title">${config.title}</div>
        <div class="ub-confirm-desc">${config.desc}</div>
        <div class="ub-confirm-card">
          <div class="ub-confirm-field">
            <label>Resident</label>
            <span><i class="fas fa-user"></i>${esc(d.residentFullName || '—')}</span>
          </div>
          <div class="ub-confirm-field">
            <label>Utility</label>
            <span><i class="fas fa-dumbbell"></i>${esc(d.utilityName || '—')}</span>
          </div>
          <div class="ub-confirm-field">
            <label>Date</label>
            <span><i class="fas fa-calendar"></i>${esc((d.startTime || '').split('·')[0].trim())}</span>
          </div>
          <div class="ub-confirm-field">
            <label>Time Slot</label>
            <span><i class="fas fa-clock"></i>${extractTimeSlot(d)}</span>
          </div>
        </div>
      </div>
      <div class="ub-confirm-footer">
        <button type="button" class="ub-btn-cancel-plain" id="ubConfirmCancel">Cancel</button>
        <button type="button" class="${config.btnClass}" id="ubConfirmProceed">${config.btnLabel}</button>
      </div>`;

    modal.querySelector('#ubConfirmCancel').addEventListener('click', closeConfirmModal);
    modal.querySelector('#ubConfirmProceed').addEventListener('click', executePendingAction);
  }

  function getConfirmConfig(newStatus, currentStatus) {
    const curr = currentStatus != null ? parseInt(currentStatus, 10) : 0;
    
    // If setting to PENDING, it means we are cancelling an APPROVED or REJECTED booking
    if (newStatus === STATUS.PENDING) {
      const isApproved = (curr === STATUS.APPROVED);
      return {
        title: isApproved ? 'Cancel Approval' : 'Cancel Rejection',
        desc: isApproved 
          ? 'You are about to cancel the approval for this utility booking. The status will return to pending.'
          : 'You are about to cancel the rejection for this utility booking. The status will return to pending.',
        btnLabel: 'Confirm Cancel',
        btnClass: 'ub-btn-confirm-cancel',
      };
    }

    switch (newStatus) {
      case STATUS.APPROVED:
        return {
          title: 'Confirm Approval',
          desc: 'Please verify the following utility booking details before final approval. This action will notify the resident and finalize the schedule.',
          btnLabel: 'Confirm Approval',
          btnClass: 'ub-btn-confirm-approve',
        };
      case STATUS.REJECTED:
        return {
          title: 'Confirm Rejection',
          desc: 'Please verify the following utility booking details before rejecting. The resident will be notified of this decision.',
          btnLabel: 'Confirm Rejection',
          btnClass: 'ub-btn-confirm-reject',
        };
      default:
        return { title: 'Confirm Action', desc: '', btnLabel: 'Confirm', btnClass: 'ub-btn-confirm-approve' };
    }
  }

  function extractTimeSlot(d) {
    const startParts = (d.startTime || '').split('·');
    const endParts   = (d.endTime   || '').split('·');
    const startT = (startParts[1] || '').trim();
    const endT   = (endParts[1]   || '').trim();
    return startT && endT ? `${startT} – ${endT}` : '—';
  }

  function closeConfirmModal() {
    confirmOverlay.classList.remove('open');
    pendingAction = null;
  }

  function executePendingAction() {
    if (!pendingAction) return;
    const { bookingId, newStatus } = pendingAction;
    const proceedBtn = document.getElementById('ubConfirmProceed');
    if (proceedBtn) { proceedBtn.disabled = true; proceedBtn.textContent = 'Processing…'; }

    const body = new URLSearchParams();
    body.set('status', newStatus);

    fetch(`${BASE_URL}/${bookingId}/status`, { method: 'POST', body })
      .then(r => r.json())
      .then(resp => {
        closeConfirmModal();
        if (resp.success) {
          // Simply reload the page to refresh the backend-rendered list and statistics
          window.location.reload();
        } else {
          showToast(resp.message || 'Operation failed.', 'error');
        }
      })
      .catch(() => { closeConfirmModal(); showToast('Network error. Please try again.', 'error'); });
  }

  // ── Confirm modal overlay close on outside click ─────────────────────────────
  function bindConfirmModal() {
    confirmOverlay.addEventListener('click', (e) => {
      if (e.target === confirmOverlay) closeConfirmModal();
    });
  }

  // ── Detail modal overlay close on outside click ──────────────────────────────
  function bindModalClose() {
    detailOverlay.addEventListener('click', (e) => {
      if (e.target === detailOverlay) closeDetailModal();
    });
  }

  // ── Toast notification ────────────────────────────────────────────────────────
  function showToast(message, type) {
    let container = document.getElementById('toastContainer');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toastContainer';
      container.style.cssText = 'position:fixed;bottom:24px;right:24px;z-index:9999;display:flex;flex-direction:column;gap:10px';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    const color = type === 'success' ? '#22c55e' : '#ef4444';
    toast.style.cssText = `background:#fff;border-left:4px solid ${color};padding:14px 18px;border-radius:8px;
      box-shadow:0 4px 16px rgba(0,0,0,.12);font-size:14px;max-width:340px;
      animation:ubSlideIn .3s ease;font-family:'Inter',sans-serif;`;
    toast.textContent = message;
    container.appendChild(toast);

    setTimeout(() => { toast.style.opacity = '0'; toast.style.transition = 'opacity .3s'; }, 3000);
    setTimeout(() => toast.remove(), 3400);
  }

  function esc(str) {
    if (str == null) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  // ── Bootstrap ─────────────────────────────────────────────────────────────────
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

})();
