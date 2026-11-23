/**
 * app.js — Jansetu Frontend
 *
 * Sections:
 *   1.  Config & state
 *   2.  DOM helpers
 *   3.  Navbar UI helpers (guest / logged-in state)
 *   4.  Auth modal (open, close, panel switch)
 *   5.  Show/hide password toggle
 *   6.  Session check on page load  (GET /api/auth/me)
 *   7.  Login form  (POST /api/auth/login)
 *   8.  Register form  (POST /api/auth/register)
 *   9.  Logout  (POST /api/auth/logout)
 *  10.  Scheme loading  (GET /api/schemes)
 *  11.  Scheme card builder + renderer
 *  12.  Search & category filter
 *  13.  Eligibility form validation & submission  (POST /api/recommendations)
 *  14.  Results renderer
 *  15.  Navbar scroll + scroll-spy
 *  16.  Mobile menu
 *  17.  Smooth scroll
 *  18.  Init
 */

'use strict';

/* =================================================================
   1. CONFIG & STATE
   ================================================================= */

/** Backend base URL — must match the Java HttpServer port */
const API_BASE = 'http://localhost:8080';

/**
 * In-memory auth state.
 * We NEVER store the session token here — the browser cookie holds it.
 * We only keep the user's display name & email (non-sensitive).
 */
let currentUser = null;   // { fullName, email } or null when logged out

/** Full list of schemes loaded from the API */
let allSchemes      = [];
let activeCategory  = 'all';
let searchQuery     = '';

/* =================================================================
   2. DOM HELPERS
   ================================================================= */

function show(id) { const el = document.getElementById(id); if (el) el.classList.remove('hidden'); }
function hide(id) { const el = document.getElementById(id); if (el) el.classList.add('hidden'); }
function $(id)    { return document.getElementById(id); }

function scrollTo(sectionId) {
  const el = $(sectionId);
  if (el) el.scrollIntoView({ behavior: 'smooth' });
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

/* =================================================================
   3. NAVBAR UI — switch between guest and logged-in state
   ================================================================= */

/**
 * Switch the navbar to the LOGGED-IN state.
 * Shows the profile button with the user's initial and name.
 * @param {{ fullName: string, email: string }} user
 */
function showLoggedInNav(user) {
  currentUser = user;

  // Fill in profile display
  const initial = (user.fullName || '?').charAt(0).toUpperCase();
  $('profileAvatar').textContent = initial;
  $('profileName').textContent   = user.fullName.split(' ')[0]; // first name only
  $('dropdownName').textContent  = user.fullName;
  $('dropdownEmail').textContent = user.email;

  // Swap nav areas
  hide('navGuest');
  show('navUser');

  // Mobile menu: hide login item, show logout item
  hide('mobileLoginItem');
  show('mobileLogoutItem');
}

/** Switch navbar to the LOGGED-OUT (guest) state. */
function showGuestNav() {
  currentUser = null;

  hide('navUser');
  show('navGuest');
  show('mobileLoginItem');
  hide('mobileLogoutItem');

  // Close profile dropdown if open
  $('profileDropdown').classList.remove('open');
  $('profileBtn').setAttribute('aria-expanded', 'false');
}

/* =================================================================
   4. AUTH MODAL — open, close, panel switch
   ================================================================= */

/** Open the auth modal on the specified panel ('login' or 'register') */
function openModal(panel = 'login') {
  show('authModal');
  document.body.style.overflow = 'hidden'; // prevent background scroll
  if (panel === 'register') {
    showRegisterPanel();
  } else {
    showLoginPanel();
  }
}

function closeModal() {
  hide('authModal');
  document.body.style.overflow = '';
  // Clear error messages on close
  clearAuthErrors();
}

function showLoginPanel() {
  show('loginPanel');
  hide('registerPanel');
  $('loginEmail').focus();
}

function showRegisterPanel() {
  hide('loginPanel');
  show('registerPanel');
  $('regName').focus();
}

/** Clear all auth form error/success messages */
function clearAuthErrors() {
  ['loginError',
   'registerError', 'registerSuccess',
   'loginEmailError', 'loginPasswordError',
   'regNameError', 'regEmailError', 'regPasswordError', 'regConfirmError'
  ].forEach(id => {
    const el = $(id);
    if (el) { el.textContent = ''; el.classList.add('hidden'); }
  });

  // Remove input-error class
  ['loginEmail','loginPassword','regName','regEmail','regPassword','regConfirm'].forEach(id => {
    const el = $(id);
    if (el) el.classList.remove('input-error');
  });
}

/* =================================================================
   5. SHOW/HIDE PASSWORD TOGGLE
   ================================================================= */

function setupPasswordToggles() {
  document.querySelectorAll('.password-toggle').forEach(btn => {
    btn.addEventListener('click', () => {
      const targetId = btn.dataset.target;
      const input    = $(targetId);
      if (!input) return;

      const isPassword = input.type === 'password';
      input.type = isPassword ? 'text' : 'password';

      btn.querySelector('.eye-show').classList.toggle('hidden',  isPassword);
      btn.querySelector('.eye-hide').classList.toggle('hidden', !isPassword);
    });
  });
}

/* =================================================================
   6. SESSION CHECK ON PAGE LOAD  —  GET /api/auth/me
   Called once when the page loads to restore login state.
   credentials:'include' is essential — it sends the HttpOnly cookie.
   ================================================================= */

async function checkSession() {
  try {
    const response = await fetch(`${API_BASE}/api/auth/me`, {
      method:      'GET',
      credentials: 'include',   // ← MUST include the session cookie
    });

    if (response.ok) {
      const data = await response.json();
      // Backend now returns { authenticated, fullName, email }
      if (data.authenticated && data.fullName) {
        showLoggedInNav({ fullName: data.fullName, email: data.email });
      } else {
        showGuestNav();
      }
    } else {
      // 401 = not logged in — perfectly normal
      showGuestNav();
    }

  } catch (err) {
    // Backend not running — show guest nav, do not pretend logged in
    console.warn('[Jansetu] Backend unreachable during session check:', err.message);
    showGuestNav();
  }
}

/* =================================================================
   7. LOGIN  —  POST /api/auth/login
   ================================================================= */

/** Validate the login form; return true if all fields pass. */
function validateLoginForm() {
  let valid = true;

  const email    = $('loginEmail').value.trim();
  const password = $('loginPassword').value;

  if (!email) {
    $('loginEmailError').textContent = 'Email address is required.';
    $('loginEmail').classList.add('input-error');
    valid = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    $('loginEmailError').textContent = 'Enter a valid email address.';
    $('loginEmail').classList.add('input-error');
    valid = false;
  }

  if (!password) {
    $('loginPasswordError').textContent = 'Password is required.';
    $('loginPassword').classList.add('input-error');
    valid = false;
  }

  return valid;
}

async function handleLoginSubmit(event) {
  event.preventDefault();
  clearAuthErrors();

  if (!validateLoginForm()) return;

  const btn = $('loginSubmitBtn');
  btn.disabled    = true;
  btn.textContent = 'Signing in…';

  try {
    const response = await fetch(`${API_BASE}/api/auth/login`, {
      method:      'POST',
      credentials: 'include',           // cookie will be set by the backend
      headers:     { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email:    $('loginEmail').value.trim().toLowerCase(),
        password: $('loginPassword').value,
      }),
    });

    const data = await response.json();

    if (response.ok && data.user) {
      // Login success: update navbar from the RESPONSE (trust server identity)
      showLoggedInNav({ fullName: data.user.fullName, email: data.user.email });
      closeModal();
      // Reset the login form for next use
      $('loginForm').reset();
    } else {
      // Show the backend error message
      const errorEl = $('loginError');
      errorEl.textContent = data.error || 'Login failed. Please check your credentials.';
      errorEl.classList.remove('hidden');
    }

  } catch (err) {
    const errorEl = $('loginError');
    errorEl.textContent = 'Could not connect to the server. Is the backend running?';
    errorEl.classList.remove('hidden');
    console.error('[Jansetu] Login error:', err);
  } finally {
    btn.disabled    = false;
    btn.textContent = 'Sign In';
  }
}

/* =================================================================
   8. REGISTER  —  POST /api/auth/register
   ================================================================= */

/** Validate registration form; return true if all fields pass. */
function validateRegisterForm() {
  let valid = true;

  const name     = $('regName').value.trim();
  const email    = $('regEmail').value.trim();
  const password = $('regPassword').value;
  const confirm  = $('regConfirm').value;

  if (!name || name.length < 2) {
    $('regNameError').textContent = 'Full name must be at least 2 characters.';
    $('regName').classList.add('input-error');
    valid = false;
  }

  if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    $('regEmailError').textContent = 'Enter a valid email address.';
    $('regEmail').classList.add('input-error');
    valid = false;
  }

  if (!password || password.length < 8) {
    $('regPasswordError').textContent = 'Password must be at least 8 characters.';
    $('regPassword').classList.add('input-error');
    valid = false;
  }

  if (!confirm) {
    $('regConfirmError').textContent = 'Please confirm your password.';
    $('regConfirm').classList.add('input-error');
    valid = false;
  } else if (password !== confirm) {
    $('regConfirmError').textContent = 'Passwords do not match.';
    $('regConfirm').classList.add('input-error');
    valid = false;
  }

  return valid;
}

async function handleRegisterSubmit(event) {
  event.preventDefault();
  clearAuthErrors();

  if (!validateRegisterForm()) return;

  const btn = $('registerSubmitBtn');
  btn.disabled    = true;
  btn.textContent = 'Creating account…';

  try {
    const response = await fetch(`${API_BASE}/api/auth/register`, {
      method:      'POST',
      credentials: 'include',
      headers:     { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        fullName: $('regName').value.trim(),
        email:    $('regEmail').value.trim().toLowerCase(),
        password: $('regPassword').value,
      }),
    });

    const data = await response.json();

    if (response.ok || response.status === 201) {
      // Show success, offer to log in
      $('registerForm').reset();
      const successEl = $('registerSuccess');
      successEl.textContent = '✓ Account created successfully! You can now sign in.';
      successEl.classList.remove('hidden');
    } else {
      // Backend returned an error (e.g. email already registered)
      const errorEl = $('registerError');
      errorEl.textContent = data.error || 'Registration failed. Please try again.';
      errorEl.classList.remove('hidden');
    }

  } catch (err) {
    const errorEl = $('registerError');
    errorEl.textContent = 'Could not connect to the server. Is the backend running?';
    errorEl.classList.remove('hidden');
    console.error('[Jansetu] Register error:', err);
  } finally {
    btn.disabled    = false;
    btn.textContent = 'Create Account';
  }
}

/* =================================================================
   9. LOGOUT  —  POST /api/auth/logout
   ================================================================= */

async function handleLogout() {
  // Close dropdown / mobile menu
  $('profileDropdown').classList.remove('open');
  closeMobileMenu();

  try {
    await fetch(`${API_BASE}/api/auth/logout`, {
      method:      'POST',
      credentials: 'include',  // sends the session cookie so backend can delete it
    });
  } catch (err) {
    console.warn('[Jansetu] Logout request failed:', err.message);
    // Even if the network request failed, update the UI to logged-out
  }

  // Always update the UI — do not leave the user looking logged in
  showGuestNav();
}

/* =================================================================
   10. SCHEME LOADING  —  GET /api/schemes
   ================================================================= */

async function loadSchemes() {
  show('schemesLoading');
  hide('schemesError');
  hide('schemesEmpty');
  $('schemesGrid').innerHTML = '';

  try {
    const response = await fetch(`${API_BASE}/api/schemes`);
    // Note: schemes endpoint is public — no credentials needed

    if (!response.ok) throw new Error(`Server error: HTTP ${response.status}`);

    allSchemes = await response.json();
    hide('schemesLoading');

    if (allSchemes.length === 0) { show('schemesEmpty'); return; }

    buildCategoryFilters(allSchemes);
    renderSchemeCards(allSchemes, 'schemesGrid');

  } catch (err) {
    console.error('[Jansetu] Failed to load schemes:', err);
    hide('schemesLoading');
    show('schemesError');
  }
}

/* =================================================================
   11. SCHEME CARD BUILDER + RENDERER
   ================================================================= */

function getCategoryBadgeClass(category) {
  const map = {
    'Agriculture':       'badge-Agriculture',
    'Education':         'badge-Education',
    'Health':            'badge-Health',
    'Housing':           'badge-Housing',
    'Business':          'badge-Business',
    'Women Welfare':     'badge-Women-Welfare',
    'Skill Development': 'badge-Skill-Development',
  };
  return map[category] || 'badge-default';
}

function getCategoryEmoji(category) {
  const map = {
    'Agriculture':       '🌾',
    'Education':         '🎓',
    'Health':            '🏥',
    'Housing':           '🏠',
    'Business':          '💼',
    'Women Welfare':     '👩',
    'Skill Development': '🛠️',
  };
  return map[category] || '📋';
}

function buildSchemeCard(scheme) {
  const badgeClass = getCategoryBadgeClass(scheme.category);
  const emoji      = getCategoryEmoji(scheme.category);
  const applyHref  = scheme.applicationLink ? escapeHtml(scheme.applicationLink) : '#';
  const disabled   = !scheme.applicationLink ? 'style="pointer-events:none;opacity:.5;"' : '';

  return `
    <article class="scheme-card" role="listitem">
      <div class="card-header">
        <h3 class="card-title">${escapeHtml(scheme.schemeName)}</h3>
        <span class="badge ${badgeClass}">${emoji} ${escapeHtml(scheme.category)}</span>
      </div>
      <p class="card-desc">${escapeHtml(scheme.description)}</p>
      <div class="card-benefits">
        <div class="card-benefits-label">Benefits</div>
        ${escapeHtml(scheme.benefits)}
      </div>
      <div class="card-footer">
        <a href="${applyHref}" target="_blank" rel="noopener noreferrer" class="btn-apply" ${disabled}>
          Apply Now
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
            <polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/>
          </svg>
        </a>
      </div>
    </article>`;
}

function renderSchemeCards(schemes, gridId) {
  const grid = $(gridId);
  if (!grid) return;
  grid.innerHTML = schemes.length === 0 ? '' : schemes.map(buildSchemeCard).join('');
}

/* =================================================================
   12. SEARCH & CATEGORY FILTER
   ================================================================= */

function buildCategoryFilters(schemes) {
  const container = $('categoryFilters');
  if (!container) return;

  const categories = [...new Set(schemes.map(s => s.category))].sort();

  container.innerHTML = `<button class="pill pill-active" data-category="all">All Schemes</button>`
    + categories.map(cat =>
        `<button class="pill" data-category="${escapeHtml(cat)}">${escapeHtml(cat)}</button>`
      ).join('');

  container.querySelectorAll('.pill').forEach(btn => {
    btn.addEventListener('click', () => handleCategoryFilter(btn));
  });
}

function handleCategoryFilter(clickedPill) {
  document.querySelectorAll('#categoryFilters .pill').forEach(p => p.classList.remove('pill-active'));
  clickedPill.classList.add('pill-active');
  activeCategory = clickedPill.dataset.category;
  applyFilters();
}

function handleSearch() {
  searchQuery = $('schemeSearch').value.trim().toLowerCase();
  applyFilters();
}

function clearFilters() {
  $('schemeSearch').value = '';
  searchQuery    = '';
  activeCategory = 'all';
  document.querySelectorAll('#categoryFilters .pill').forEach(p => {
    p.classList.toggle('pill-active', p.dataset.category === 'all');
  });
  applyFilters();
}

function applyFilters() {
  let filtered = allSchemes;
  if (activeCategory !== 'all') {
    filtered = filtered.filter(s => s.category === activeCategory);
  }
  if (searchQuery) {
    filtered = filtered.filter(s =>
      s.schemeName.toLowerCase().includes(searchQuery) ||
      (s.description && s.description.toLowerCase().includes(searchQuery)) ||
      (s.benefits    && s.benefits.toLowerCase().includes(searchQuery))
    );
  }
  if (filtered.length === 0) {
    $('schemesGrid').innerHTML = '';
    show('schemesEmpty');
  } else {
    hide('schemesEmpty');
    renderSchemeCards(filtered, 'schemesGrid');
  }
}

/* =================================================================
   13. ELIGIBILITY FORM VALIDATION & SUBMISSION
       POST /api/recommendations
   ================================================================= */

function validateEligibilityForm() {
  let valid = true;

  // Clear previous errors
  ['nameError','ageError','stateError','professionError',
   'incomeError','educationLevelError','genderError','maritalStatusError'].forEach(id => {
    const el = $(id); if (el) el.textContent = '';
  });
  ['name','age','state','profession','income','educationLevel','gender','maritalStatus'].forEach(id => {
    const el = $(id); if (el) el.classList.remove('input-error');
  });

  function setErr(inputId, errorId, msg) {
    const inp = $(inputId), err = $(errorId);
    if (inp) inp.classList.add('input-error');
    if (err) err.textContent = msg;
    valid = false;
  }

  const name = $('name').value.trim();
  if (!name || name.length < 2)         setErr('name',           'nameError',           'Full name is required (min 2 characters).');

  const ageVal = $('age').value;
  const age    = parseInt(ageVal, 10);
  if (!ageVal)                           setErr('age',            'ageError',            'Age is required.');
  else if (isNaN(age) || age < 1 || age > 120) setErr('age',     'ageError',            'Enter a valid age between 1 and 120.');

  if (!$('state').value)                 setErr('state',          'stateError',          'Please select your state.');
  if (!$('profession').value)            setErr('profession',     'professionError',     'Please select your profession.');

  const incomeVal = $('income').value;
  const income    = parseFloat(incomeVal);
  if (incomeVal === '')                  setErr('income',         'incomeError',         'Annual income is required.');
  else if (isNaN(income) || income < 0) setErr('income',         'incomeError',         'Enter a valid non-negative income amount.');

  if (!$('educationLevel').value)        setErr('educationLevel', 'educationLevelError', 'Please select your education level.');
  if (!$('gender').value)                setErr('gender',         'genderError',         'Please select your gender.');
  if (!$('maritalStatus').value)         setErr('maritalStatus',  'maritalStatusError',  'Please select your marital status.');

  return valid;
}

async function handleEligibilitySubmit(event) {
  event.preventDefault();

  if (!validateEligibilityForm()) {
    const firstErr = document.querySelector('.field-error:not(:empty)');
    if (firstErr) firstErr.scrollIntoView({ behavior: 'smooth', block: 'center' });
    return;
  }

  // Build the payload — age & income must be numbers, not strings
  const payload = {
    name:           $('name').value.trim(),
    age:            parseInt($('age').value, 10),
    state:          $('state').value,
    profession:     $('profession').value,
    income:         parseFloat($('income').value),
    educationLevel: $('educationLevel').value,
    gender:         $('gender').value,
    maritalStatus:  $('maritalStatus').value,
  };

  console.log('[Jansetu] Sending recommendation request:', payload);

  // Show results section in loading state
  show('results');
  show('resultsLoading');
  hide('resultsContent');
  hide('resultsEmpty');
  hide('resultsError');
  scrollTo('results');

  const submitBtn = $('submitBtn');
  submitBtn.disabled = true;
  submitBtn.textContent = 'Finding Schemes…';

  try {
    const response = await fetch(`${API_BASE}/api/recommendations`, {
      method:      'POST',
      credentials: 'include',
      headers:     { 'Content-Type': 'application/json' },
      body:        JSON.stringify(payload),
    });

    if (!response.ok) throw new Error(`Server error: HTTP ${response.status}`);

    const data = await response.json();
    console.log('[Jansetu] Received recommendations:', data);

    const recommendations = data.recommendations || [];
    hide('resultsLoading');

    if (recommendations.length === 0) {
      show('resultsEmpty');
    } else {
      displayResults(recommendations, payload.name);
    }

  } catch (err) {
    console.error('[Jansetu] Recommendation error:', err);
    hide('resultsLoading');
    $('resultsErrorMsg').textContent = err.message || 'An unknown error occurred.';
    show('resultsError');
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerHTML = `
      <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"
           stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
      </svg>
      Find My Eligible Schemes`;
  }
}

function resetEligibilityForm() {
  $('eligibilityForm').reset();
  ['nameError','ageError','stateError','professionError',
   'incomeError','educationLevelError','genderError','maritalStatusError'].forEach(id => {
    const el = $(id); if (el) el.textContent = '';
  });
  ['name','age','state','profession','income','educationLevel','gender','maritalStatus'].forEach(id => {
    const el = $(id); if (el) el.classList.remove('input-error');
  });
  hide('results');
}

/* =================================================================
   14. RESULTS RENDERER
   ================================================================= */

function displayResults(recommendations, name) {
  const count = recommendations.length;
  $('resultsTitle').textContent    = `${count} Scheme${count !== 1 ? 's' : ''} Found for ${name}`;
  $('resultsSubtitle').textContent = `Based on your profile, you are eligible for the following ${count === 1 ? 'scheme' : 'schemes'}.`;
  renderSchemeCards(recommendations, 'resultsGrid');
  show('resultsContent');
  scrollTo('results');
}

/* =================================================================
   15. NAVBAR SCROLL + SCROLL-SPY
   ================================================================= */

function setupNavbarScroll() {
  const navbar = $('navbar');
  window.addEventListener('scroll', () => {
    navbar.classList.toggle('scrolled', window.scrollY > 10);
  }, { passive: true });
}

function setupScrollSpy() {
  const sections = document.querySelectorAll('main section[id]');
  const navLinks = document.querySelectorAll('.nav-link');
  if (!sections.length || !navLinks.length) return;

  const observer = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        navLinks.forEach(l => l.classList.remove('active'));
        const active = document.querySelector(`.nav-link[href="#${entry.target.id}"]`);
        if (active) active.classList.add('active');
      }
    });
  }, { rootMargin: '-50% 0px -45% 0px', threshold: 0 });

  sections.forEach(s => observer.observe(s));
}

/* =================================================================
   16. MOBILE MENU
   ================================================================= */

function closeMobileMenu() {
  $('mobileMenu').classList.remove('open');
  $('hamburger').classList.remove('open');
  $('hamburger').setAttribute('aria-expanded', 'false');
}

function setupMobileMenu() {
  const hamburger  = $('hamburger');
  const mobileMenu = $('mobileMenu');
  if (!hamburger || !mobileMenu) return;

  hamburger.addEventListener('click', () => {
    const isOpen = mobileMenu.classList.toggle('open');
    hamburger.classList.toggle('open', isOpen);
    hamburger.setAttribute('aria-expanded', String(isOpen));
  });

  document.querySelectorAll('.mobile-nav-link').forEach(link => {
    link.addEventListener('click', closeMobileMenu);
  });

  // Click outside to close
  document.addEventListener('click', e => {
    if (!hamburger.contains(e.target) && !mobileMenu.contains(e.target)) {
      closeMobileMenu();
    }
  });
}

/* =================================================================
   17. SMOOTH SCROLL for all # links
   ================================================================= */

function setupSmoothScroll() {
  document.querySelectorAll('a[href^="#"]').forEach(link => {
    link.addEventListener('click', e => {
      const id = link.getAttribute('href').slice(1);
      const el = $(id);
      if (el) { e.preventDefault(); el.scrollIntoView({ behavior: 'smooth' }); }
    });
  });
}

/* =================================================================
   18. INIT
   ================================================================= */

function init() {
  console.log('[Jansetu] Initialising…');

  /* --- Auth modal wiring --- */
  $('navLoginBtn')?.addEventListener('click', () => openModal('login'));
  $('mobileLoginBtn')?.addEventListener('click', () => { closeMobileMenu(); openModal('login'); });
  $('modalClose')?.addEventListener('click', closeModal);
  $('switchToRegister')?.addEventListener('click', () => { clearAuthErrors(); showRegisterPanel(); });
  $('switchToLogin')?.addEventListener('click',    () => { clearAuthErrors(); showLoginPanel(); });

  // Close modal when clicking the dark overlay (outside the card)
  $('authModal')?.addEventListener('click', e => { if (e.target === $('authModal')) closeModal(); });

  // Close modal on Escape key
  document.addEventListener('keydown', e => { if (e.key === 'Escape' && !$('authModal').classList.contains('hidden')) closeModal(); });

  /* --- Auth form submissions --- */
  $('loginForm')?.addEventListener('submit', handleLoginSubmit);
  $('registerForm')?.addEventListener('submit', handleRegisterSubmit);

  /* --- Profile dropdown --- */
  $('profileBtn')?.addEventListener('click', e => {
    e.stopPropagation();
    const dropdown = $('profileDropdown');
    const isOpen   = dropdown.classList.toggle('open');
    $('profileBtn').setAttribute('aria-expanded', String(isOpen));
  });
  // Close dropdown when clicking outside
  document.addEventListener('click', e => {
    if (!$('profileMenuWrapper')?.contains(e.target)) {
      $('profileDropdown')?.classList.remove('open');
      $('profileBtn')?.setAttribute('aria-expanded', 'false');
    }
  });

  /* --- Logout --- */
  $('logoutBtn')?.addEventListener('click', handleLogout);
  $('mobileLogoutBtn')?.addEventListener('click', () => { closeMobileMenu(); handleLogout(); });

  /* --- Show/hide password --- */
  setupPasswordToggles();

  /* --- Schemes --- */
  loadSchemes();
  $('schemeSearch')?.addEventListener('input', handleSearch);

  /* --- Eligibility form --- */
  $('eligibilityForm')?.addEventListener('submit', handleEligibilitySubmit);
  $('resetBtn')?.addEventListener('click', resetEligibilityForm);

  /* --- Navbar & nav --- */
  setupNavbarScroll();
  setupScrollSpy();
  setupMobileMenu();
  setupSmoothScroll();

  /* --- Check existing session (restores login after page refresh) --- */
  checkSession();

  console.log('[Jansetu] Ready. Backend:', API_BASE);
}

document.addEventListener('DOMContentLoaded', init);
