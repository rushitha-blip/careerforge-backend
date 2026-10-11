// CareerForge AI Resume Builder & ATS Evaluator

function evaluateResumeAtsScore() {
    const summaryInput = document.getElementById('resumeSummaryInput');
    const skillsInput = document.getElementById('resumeSkillsInput');
    const templateSelect = document.getElementById('resumeTemplateSelect');

    const summary = summaryInput ? summaryInput.value : '';
    const skillsCsv = skillsInput ? skillsInput.value : '';
    const templateName = templateSelect ? templateSelect.value : 'Modern Standard';

    fetch('/api/student/resume/evaluate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ summary, skillsCsv, templateName })
    })
    .then(res => res.json())
    .then(data => {
        document.getElementById('atsScoreBadge').innerText = data.atsScore + ' / 100';
        document.getElementById('atsSuggestionsBox').innerText = data.atsSuggestions;
        document.getElementById('previewSummary').innerText = data.summary;
        document.getElementById('previewSkills').innerText = data.skillsCsv;
        
        Swal.fire({
            title: 'ATS Resume Evaluated!',
            text: `Updated Score: ${data.atsScore}% Compatibility`,
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
        });
    })
    .catch(err => console.error('Resume eval failed:', err));
}

function printFormattedResume() {
    window.print();
}
