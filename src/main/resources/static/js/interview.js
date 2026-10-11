// CareerForge AI Mock Interview Quiz Engine

let quizQuestions = [];
let quizAnswers = {};
let quizCategory = 'ALL';
let timerInterval = null;
let secondsRemaining = 600;

function startMockInterview(category) {
    quizCategory = category;
    quizAnswers = {};
    secondsRemaining = 600;

    fetch(`/api/student/interview/questions?category=${category}`)
        .then(res => res.json())
        .then(data => {
            quizQuestions = data;
            if (!quizQuestions || quizQuestions.length === 0) {
                Swal.fire('Notice', 'No questions found for category: ' + category, 'info');
                return;
            }
            renderQuizQuestions();
            startQuizTimer();
            const modal = new bootstrap.Modal(document.getElementById('mockInterviewModal'));
            modal.show();
        });
}

function renderQuizQuestions() {
    const container = document.getElementById('quizContainer');
    container.innerHTML = '';

    quizQuestions.forEach((q, idx) => {
        const qCard = document.createElement('div');
        qCard.className = 'card card-saas mb-4 p-4';
        qCard.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="badge bg-primary">Question ${idx + 1} of ${quizQuestions.length}</span>
                <span class="badge bg-secondary">${q.category} - ${q.topic}</span>
            </div>
            <h5 class="fw-bold mb-3">${q.question}</h5>
            <div class="quiz-option-card">
                <input type="radio" name="q_${q.id}" id="q_${q.id}_A" value="A" onchange="recordAnswer(${q.id}, 'A')">
                <label for="q_${q.id}_A" class="ms-2">A) ${q.optionA}</label>
            </div>
            <div class="quiz-option-card">
                <input type="radio" name="q_${q.id}" id="q_${q.id}_B" value="B" onchange="recordAnswer(${q.id}, 'B')">
                <label for="q_${q.id}_B" class="ms-2">B) ${q.optionB}</label>
            </div>
            <div class="quiz-option-card">
                <input type="radio" name="q_${q.id}" id="q_${q.id}_C" value="C" onchange="recordAnswer(${q.id}, 'C')">
                <label for="q_${q.id}_C" class="ms-2">C) ${q.optionC}</label>
            </div>
            <div class="quiz-option-card">
                <input type="radio" name="q_${q.id}" id="q_${q.id}_D" value="D" onchange="recordAnswer(${q.id}, 'D')">
                <label for="q_${q.id}_D" class="ms-2">D) ${q.optionD}</label>
            </div>
        `;
        container.appendChild(qCard);
    });
}

function recordAnswer(qId, option) {
    quizAnswers[qId] = option;
}

function startQuizTimer() {
    clearInterval(timerInterval);
    timerInterval = setInterval(() => {
        secondsRemaining--;
        const mins = Math.floor(secondsRemaining / 60);
        const secs = secondsRemaining % 60;
        const timerEl = document.getElementById('quizTimer');
        if (timerEl) {
            timerEl.innerText = `${mins}:${secs < 10 ? '0' : ''}${secs}`;
        }
        if (secondsRemaining <= 0) {
            clearInterval(timerInterval);
            submitMockInterview();
        }
    }, 1000);
}

function submitMockInterview() {
    clearInterval(timerInterval);

    fetch('/api/student/interview/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            category: quizCategory,
            answers: quizAnswers
        })
    })
    .then(res => res.json())
    .then(data => {
        const modalEl = document.getElementById('mockInterviewModal');
        const modal = bootstrap.Modal.getInstance(modalEl);
        if (modal) modal.hide();

        Swal.fire({
            title: `Interview Complete! Score: ${data.overallScore}%`,
            html: `
                <div class="text-start">
                    <p><strong>Technical Score:</strong> ${data.technicalScore}%</p>
                    <p><strong>HR Score:</strong> ${data.hrScore}%</p>
                    <p><strong>Aptitude Score:</strong> ${data.aptitudeScore}%</p>
                    <hr>
                    <p><strong>Weak Areas:</strong> ${data.weakAreas}</p>
                    <p><strong>Feedback:</strong> ${data.feedback}</p>
                </div>
            `,
            icon: 'success',
            confirmButtonText: 'Return to Dashboard'
        }).then(() => {
            window.location.reload();
        });
    });
}
