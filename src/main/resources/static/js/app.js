// CareerForge AI App Helpers & Theme Manager

document.addEventListener('DOMContentLoaded', () => {
    // Theme Toggle Handler
    const savedTheme = localStorage.getItem('careerforge_theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);

    const themeToggleBtn = document.getElementById('themeToggleBtn');
    if (themeToggleBtn) {
        themeToggleBtn.addEventListener('click', () => {
            const currentTheme = document.documentElement.getAttribute('data-theme');
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('careerforge_theme', newTheme);
            updateThemeIcon(newTheme);
        });
    }

    // Auto tab switch based on URL hash
    const hash = window.location.hash;
    if (hash) {
        const triggerEl = document.querySelector(`.nav-link-custom[href="${hash}"], button[data-bs-target="${hash}"]`);
        if (triggerEl) {
            const tab = new bootstrap.Tab(triggerEl);
            tab.show();
        }
    }
});

function updateThemeIcon(theme) {
    const icon = document.querySelector('#themeToggleBtn i');
    if (icon) {
        if (theme === 'dark') {
            icon.className = 'fas fa-sun';
        } else {
            icon.className = 'fas fa-moon';
        }
    }
}
