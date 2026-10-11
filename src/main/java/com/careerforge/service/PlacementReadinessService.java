package com.careerforge.service;

import com.careerforge.dto.ReadinessScoreDto;
import com.careerforge.model.*;
import com.careerforge.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlacementReadinessService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private MockInterviewRepository mockInterviewRepository;

    @Autowired
    private ResumeDataRepository resumeDataRepository;

    @Transactional
    public ReadinessScoreDto calculateAndUpdateReadinessScore(StudentProfile profile) {
        if (profile == null) {
            return new ReadinessScoreDto(0, 0, 0, 0, 0, 0, 0, "Needs Focus", List.of("Complete student profile to compute readiness score."));
        }

        // 1. Technical Skills (40% Weightage - Max 40 Points)
        List<StudentSkill> skills = studentSkillRepository.findByStudentProfile(profile);
        int techScore = 0;
        for (StudentSkill ss : skills) {
            String p = ss.getProficiency() != null ? ss.getProficiency().toUpperCase() : "BEGINNER";
            if ("ADVANCED".equals(p)) techScore += 8;
            else if ("INTERMEDIATE".equals(p)) techScore += 5;
            else techScore += 3;
        }
        techScore = Math.min(40, techScore);

        // 2. Coding & Aptitude Practice (20% Weightage - Max 20 Points)
        List<MockInterview> interviews = mockInterviewRepository.findByStudentProfileOrderByTakenAtDesc(profile);
        int codingAptitudeScore = 0;
        if (!interviews.isEmpty()) {
            double avgScore = interviews.stream()
                    .mapToInt(mi -> (mi.getAptitudeScore() != null ? mi.getAptitudeScore() : mi.getOverallScore()))
                    .average().orElse(0.0);
            codingAptitudeScore = (int) Math.round((avgScore / 100.0) * 20);
            codingAptitudeScore = Math.min(20, codingAptitudeScore);
        }

        // 3. Projects (15% Weightage - Max 15 Points)
        long projectCount = projectRepository.countByStudentProfile(profile);
        int projectScore = 0;
        if (projectCount >= 3) {
            projectScore = 15;
        } else if (projectCount == 2) {
            projectScore = 11;
        } else if (projectCount == 1) {
            projectScore = 6;
        }

        // 4. Resume Completeness & ATS (15% Weightage - Max 15 Points)
        ResumeData resume = resumeDataRepository.findByStudentProfile(profile).orElse(null);
        int resumeScore = 0;
        if (resume != null && resume.getAtsScore() != null) {
            resumeScore = (int) Math.round((resume.getAtsScore() / 100.0) * 15);
            resumeScore = Math.min(15, resumeScore);
        } else {
            resumeScore = 4; // Base score for having entered education/contact
        }

        // 5. Communication & Interview Practice (10% Weightage - Max 10 Points)
        int interviewScore = 0;
        if (!interviews.isEmpty()) {
            double avgComm = interviews.stream()
                    .mapToInt(mi -> (mi.getHrScore() != null ? mi.getHrScore() : mi.getOverallScore()))
                    .average().orElse(0.0);
            interviewScore = (int) Math.round((avgComm / 100.0) * 10);
            interviewScore = Math.min(10, interviewScore);
        }

        // Certifications Credential count
        long certCount = certificationRepository.countByStudentProfile(profile);
        int certScore = Math.min(15, (int) (certCount * 5));

        int totalScore = techScore + codingAptitudeScore + projectScore + resumeScore + interviewScore;
        totalScore = Math.min(100, Math.max(0, totalScore));

        profile.setTechSkillScore(techScore);
        profile.setCodingAptitudeScore(codingAptitudeScore);
        profile.setProjectScore(projectScore);
        profile.setResumeScore(resumeScore);
        profile.setCertScore(certScore);
        profile.setInterviewScore(interviewScore);
        profile.setReadinessScore(totalScore);

        studentProfileRepository.save(profile);

        String status = "Needs Focus";
        if (totalScore >= 75) {
            status = "Placement Ready";
        } else if (totalScore >= 55) {
            status = "High Potential";
        }

        List<String> feedback = new ArrayList<>();
        if (techScore < 25) {
            feedback.add("Add and verify more core technical skills to improve your Technical score (target 30+/40).");
        }
        if (codingAptitudeScore < 12) {
            feedback.add("Practice timed Aptitude & Coding Quizzes to earn up to 20 points for placement drives.");
        }
        if (projectCount < 2) {
            feedback.add("Build at least 2 full-stack projects to reach maximum project score.");
        }
        if (resumeScore < 11) {
            feedback.add("Optimize your resume keywords using the ATS Resume Evaluator.");
        }
        if (interviewScore < 7) {
            feedback.add("Take at least 1 mock interview session to unlock communication and behavioral points.");
        }
        if (feedback.isEmpty()) {
            feedback.add("Outstanding placement readiness! You meet the criteria for top-tier hiring drives.");
        }

        return new ReadinessScoreDto(totalScore, techScore, codingAptitudeScore, projectScore, resumeScore, certScore, interviewScore, status, feedback);
    }
}
