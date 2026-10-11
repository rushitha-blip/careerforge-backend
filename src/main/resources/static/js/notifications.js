// CareerForge AI - Live Notification Center & Audio Chime Engine

const DEFAULT_NOTIFICATIONS = [
    {
        id: 1,
        title: "Placement Readiness Evaluated",
        message: "Your profile scored 83/100 (Placement Ready)! Technical and Project pillars verified.",
        type: "success",
        time: "Just now",
        unread: true,
        icon: "fas fa-check-circle text-success"
    },
    {
        id: 2,
        title: "New Recruiter Job Match",
        message: "TechCorp Global Solutions posted 'Java Full Stack Developer' (₹8.5 - ₹12 LPA) matching your skills.",
        type: "info",
        time: "15 mins ago",
        unread: true,
        icon: "fas fa-briefcase text-info"
    },
    {
        id: 3,
        title: "Skill Gap Benchmark Alert",
        message: "Missing prerequisite for Java Developer: Spring MVC & Hibernate. View your custom learning roadmap.",
        type: "warning",
        time: "1 hour ago",
        unread: true,
        icon: "fas fa-exclamation-triangle text-warning"
    },
    {
        id: 4,
        title: "Achievement Badges Unlocked",
        message: "Congratulations! You earned 'Java Champion' and 'ATS Resume Master' badges.",
        type: "primary",
        time: "2 hours ago",
        unread: false,
        icon: "fas fa-award text-primary"
    },
    {
        id: 5,
        title: "Mock Interview Recommended",
        message: "Take the 10-minute Timed Technical Quiz to unlock maximum interview readiness points.",
        type: "secondary",
        time: "Yesterday",
        unread: false,
        icon: "fas fa-laptop-code text-secondary"
    }
];

// Web Audio API Synthesized Chime (No external mp3 required!)
function playNotificationChime() {
    try {
        const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        const osc = audioCtx.createOscillator();
        const gainNode = audioCtx.createGain();

        osc.type = 'sine';
        osc.frequency.setValueAtTime(587.33, audioCtx.currentTime); // D5
        osc.frequency.exponentialRampToValueAtTime(880.00, audioCtx.currentTime + 0.15); // A5

        gainNode.gain.setValueAtTime(0.3, audioCtx.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + 0.35);

        osc.connect(gainNode);
        gainNode.connect(audioCtx.destination);

        osc.start();
        osc.stop(audioCtx.currentTime + 0.35);
    } catch (e) {
        console.log('Audio chime not permitted by browser policy until user interaction:', e);
    }
}

function getStoredNotifications() {
    const raw = localStorage.getItem('careerforge_notifications');
    if (!raw) {
        localStorage.setItem('careerforge_notifications', JSON.stringify(DEFAULT_NOTIFICATIONS));
        return DEFAULT_NOTIFICATIONS;
    }
    try {
        return JSON.parse(raw);
    } catch (e) {
        return DEFAULT_NOTIFICATIONS;
    }
}

function renderNotifications() {
    const list = getStoredNotifications();
    const container = document.getElementById('notificationsListContainer');
    const badge = document.getElementById('notificationBadgeCount');

    const unreadCount = list.filter(n => n.unread).length;

    if (badge) {
        if (unreadCount > 0) {
            badge.innerText = unreadCount;
            badge.classList.remove('d-none');
        } else {
            badge.classList.add('d-none');
        }
    }

    if (!container) return;

    if (list.length === 0) {
        container.innerHTML = `
            <div class="p-4 text-center text-muted">
                <i class="fas fa-bell-slash fa-2x mb-2 opacity-50"></i>
                <p class="mb-0 small">No notifications right now.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = list.map(item => `
        <div class="notification-item p-3 border-bottom ${item.unread ? 'bg-primary-subtle bg-opacity-10' : ''}" id="notif-item-${item.id}">
            <div class="d-flex align-items-start gap-3">
                <div class="fs-5 mt-1"><i class="${item.icon}"></i></div>
                <div class="flex-grow-1">
                    <div class="d-flex justify-content-between align-items-center">
                        <h6 class="fw-bold mb-1 fs-6">${item.title}</h6>
                        <small class="text-muted" style="font-size: 0.75rem;">${item.time}</small>
                    </div>
                    <p class="small text-secondary mb-1">${item.message}</p>
                    ${item.unread ? `<span class="badge bg-primary" style="font-size: 0.7rem;">New</span>` : ''}
                </div>
            </div>
        </div>
    `).join('');
}

function markAllNotificationsRead() {
    const list = getStoredNotifications();
    list.forEach(n => n.unread = false);
    localStorage.setItem('careerforge_notifications', JSON.stringify(list));
    renderNotifications();
    Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'success',
        title: 'All notifications marked as read',
        showConfirmButton: false,
        timer: 2000
    });
}

function clearAllNotifications() {
    localStorage.setItem('careerforge_notifications', JSON.stringify([]));
    renderNotifications();
    Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'info',
        title: 'All notifications cleared',
        showConfirmButton: false,
        timer: 2000
    });
}

function triggerLiveNotificationAlert(title, message, type = 'success') {
    playNotificationChime();

    const list = getStoredNotifications();
    const newNotif = {
        id: Date.now(),
        title: title || 'Live System Alert',
        message: message || 'Skill gap recalculated and saved.',
        type: type,
        time: 'Just now',
        unread: true,
        icon: type === 'success' ? 'fas fa-check-circle text-success' : 'fas fa-info-circle text-info'
    };

    list.unshift(newNotif);
    localStorage.setItem('careerforge_notifications', JSON.stringify(list));
    renderNotifications();

    Swal.fire({
        toast: true,
        position: 'top-end',
        icon: type,
        title: newNotif.title,
        text: newNotif.message,
        showConfirmButton: false,
        timer: 4000,
        timerProgressBar: true
    });
}

document.addEventListener('DOMContentLoaded', () => {
    renderNotifications();
});
