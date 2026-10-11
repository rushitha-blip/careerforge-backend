package com.careerforge.service;

import com.careerforge.model.Project;
import com.careerforge.model.ResumeData;
import com.careerforge.model.StudentProfile;
import com.careerforge.model.StudentSkill;
import com.careerforge.repository.ProjectRepository;
import com.careerforge.repository.ResumeDataRepository;
import com.careerforge.repository.StudentSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    @Autowired
    private ResumeDataRepository resumeDataRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ResumeData getOrCreateResumeData(StudentProfile profile) {
        return resumeDataRepository.findByStudentProfile(profile)
                .orElseGet(() -> {
                    List<StudentSkill> skills = studentSkillRepository.findByStudentProfile(profile);
                    String skillsCsv = skills.stream()
                            .map(s -> s.getSkill().getName())
                            .collect(Collectors.joining(", "));

                    List<Project> projects = projectRepository.findByStudentProfile(profile);
                    String summary = "Motivated " + (profile.getBranch() != null ? profile.getBranch() : "Engineering") + 
                            " student passionate about building scalable web applications and solving complex algorithmic challenges.";

                    ResumeData newResume = new ResumeData(
                            profile,
                            summary,
                            78, // Initial ATS score
                            "Modern Standard",
                            skillsCsv,
                            "[{\"company\":\"Tech Intern\",\"role\":\"Software Intern\",\"duration\":\"3 Months\",\"desc\":\"Assisted in developing REST APIs using Java and Spring Boot.\"}]",
                            "[{\"degree\":\"B.Tech in " + profile.getBranch() + "\",\"institution\":\"State Engineering College\",\"year\":\"" + profile.getYear() + "\",\"cgpa\":\"" + profile.getCgpa() + "\"}]",
                            "[{\"title\":\"Placement Readiness System\",\"tech\":\"Spring Boot, MySQL, Bootstrap\",\"desc\":\"Built automated career analytics web platform.\"}]",
                            "Include action verbs (e.g. Optimized, Architected). Add metrics to project descriptions."
                    );
                    return resumeDataRepository.save(newResume);
                });
    }

    public ResumeData updateAndEvaluateResume(StudentProfile profile, String summary, String skillsCsv, String templateName) {
        ResumeData resume = getOrCreateResumeData(profile);
        resume.setSummary(summary);
        resume.setSkillsCsv(skillsCsv);
        if (templateName != null) resume.setTemplateName(templateName);

        // Algorithmic ATS Score Calculation
        int atsScore = 50; // Base score
        if (summary != null && summary.length() > 50) atsScore += 15;
        if (skillsCsv != null && skillsCsv.split(",").length >= 5) atsScore += 20;
        if (profile.getCgpa() != null && profile.getCgpa() >= 7.5) atsScore += 15;

        atsScore = Math.min(98, atsScore);
        resume.setAtsScore(atsScore);

        String suggestions = "";
        if (atsScore < 70) suggestions = "Add more industry keywords (e.g. REST API, Git, SQL, Agile). Ensure contact details and LinkedIn URL are present.";
        else if (atsScore < 85) suggestions = "Quantify project accomplishments (e.g., 'Improved API latency by 30%').";
        else suggestions = "Outstanding resume! High ATS compatibility score achieved.";

        resume.setAtsSuggestions(suggestions);

        return resumeDataRepository.save(resume);
    }
}
