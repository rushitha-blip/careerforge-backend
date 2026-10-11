// CareerForge AI - Analytics & Chart.js Visualizer

function initReadinessRadarChart(canvasId, tech, project, resume, cert, interview) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    new Chart(ctx, {
        type: 'radar',
        data: {
            labels: ['Technical Skills (40%)', 'Projects (20%)', 'Resume Quality (15%)', 'Certifications (15%)', 'Mock Interview (10%)'],
            datasets: [{
                label: 'Student Readiness Factors',
                data: [tech, project, resume, cert, interview],
                backgroundColor: 'rgba(79, 70, 229, 0.25)',
                borderColor: '#4f46e5',
                borderWidth: 2,
                pointBackgroundColor: '#4f46e5',
                pointBorderColor: '#fff',
                pointHoverBackgroundColor: '#fff',
                pointHoverBorderColor: '#4f46e5'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                r: {
                    angleLines: { color: 'rgba(148, 163, 184, 0.2)' },
                    grid: { color: 'rgba(148, 163, 184, 0.2)' },
                    suggestedMin: 0,
                    suggestedMax: 40
                }
            }
        }
    });
}

function initSkillGapBarChart(canvasId, requiredSkills, ownedSkills) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const dataOwned = requiredSkills.map(req => ownedSkills.includes(req) ? 100 : 0);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: requiredSkills,
            datasets: [{
                label: 'Your Mastery %',
                data: dataOwned,
                backgroundColor: dataOwned.map(val => val === 100 ? 'rgba(16, 185, 129, 0.85)' : 'rgba(239, 68, 68, 0.85)'),
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: { beginAtZero: true, max: 100 }
            }
        }
    });
}

function initAdminTrendChart(canvasId) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct'],
            datasets: [{
                label: 'Average Placement Readiness Score',
                data: [62, 65, 68, 72, 74, 76, 79, 81, 83, 85],
                borderColor: '#06b6d4',
                backgroundColor: 'rgba(6, 182, 212, 0.15)',
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });
}

// Global Radar Chart Instance
let skillRadarChartInstance = null;

function renderSkillRadarChart(canvasId, role = 'Full Stack Developer') {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const roleBenchmarks = {
        'Full Stack Developer': {
            labels: ['Core Java', 'Spring Boot', 'SQL & MySQL', 'Frontend (HTML/CSS/JS)', 'REST APIs', 'System Design'],
            student: [90, 75, 88, 82, 85, 65],
            industry: [85, 80, 80, 85, 90, 75]
        },
        'Java Developer': {
            labels: ['Core Java', 'Spring Boot', 'Hibernate / JPA', 'MySQL', 'Multithreading', 'Design Patterns'],
            student: [92, 75, 70, 88, 80, 72],
            industry: [90, 85, 80, 80, 85, 80]
        },
        'Software Engineer': {
            labels: ['Data Structures', 'Algorithms', 'Java OOP', 'System Design', 'Git & CI/CD', 'Problem Solving'],
            student: [85, 80, 90, 68, 85, 85],
            industry: [90, 90, 85, 75, 80, 90]
        },
        'Backend Developer': {
            labels: ['Java Microservices', 'Spring Security', 'Distributed SQL', 'Docker & Cloud', 'REST / gRPC', 'Caching (Redis)'],
            student: [78, 70, 85, 60, 88, 65],
            industry: [85, 85, 85, 75, 90, 80]
        },
        'Data Scientist': {
            labels: ['Python', 'SQL & Queries', 'Data Analysis', 'Machine Learning', 'Statistics', 'Visualization'],
            student: [70, 88, 65, 55, 75, 70],
            industry: [90, 85, 90, 85, 90, 80]
        },
        'AI Engineer': {
            labels: ['Python', 'Deep Learning', 'PyTorch / TF', 'Prompt Eng & LLMs', 'Data Pipelines', 'Model Deployment'],
            student: [65, 50, 45, 60, 70, 55],
            industry: [90, 85, 85, 85, 80, 80]
        }
    };

    const benchmark = roleBenchmarks[role] || roleBenchmarks['Full Stack Developer'];

    if (skillRadarChartInstance) {
        skillRadarChartInstance.destroy();
    }

    skillRadarChartInstance = new Chart(ctx, {
        type: 'radar',
        data: {
            labels: benchmark.labels,
            datasets: [
                {
                    label: 'Your Verified Skill Mastery (%)',
                    data: benchmark.student,
                    backgroundColor: 'rgba(99, 102, 241, 0.35)',
                    borderColor: '#6366f1',
                    borderWidth: 2.5,
                    pointBackgroundColor: '#6366f1',
                    pointBorderColor: '#ffffff',
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'Industry Benchmark Standard (%)',
                    data: benchmark.industry,
                    backgroundColor: 'rgba(6, 182, 212, 0.2)',
                    borderColor: '#06b6d4',
                    borderWidth: 2,
                    borderDash: [5, 5],
                    pointBackgroundColor: '#06b6d4',
                    pointBorderColor: '#ffffff',
                    pointRadius: 3,
                    pointHoverRadius: 5
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                r: {
                    angleLines: { color: 'rgba(255, 255, 255, 0.1)' },
                    grid: { color: 'rgba(255, 255, 255, 0.1)' },
                    pointLabels: {
                        color: '#cbd5e1',
                        font: { size: 12, weight: '600' }
                    },
                    ticks: {
                        color: '#94a3b8',
                        backdropColor: 'transparent',
                        stepSize: 20
                    },
                    suggestedMin: 0,
                    suggestedMax: 100
                }
            },
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        color: '#f8fafc',
                        font: { size: 12, weight: '500' }
                    }
                }
            }
        }
    });
}

