package com.careerforge.controller;

import com.careerforge.dto.JobMatchDto;
import com.careerforge.dto.ReadinessScoreDto;
import com.careerforge.dto.SkillGapResultDto;
import com.careerforge.model.*;
import com.careerforge.repository.*;
import com.careerforge.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LandingController {

    @Autowired
    private AuthService authService;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PlacementReadinessService placementReadinessService;

    @Autowired
    private SkillGapService skillGapService;

    @Autowired
    private CareerAdvisorService careerAdvisorService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private MockInterviewService mockInterviewService;

    @Autowired
    private JobService jobService;

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @GetMapping("/")
    public String masterApp(@RequestParam(defaultValue = "STUDENT") String activeRole,
                            HttpSession session, Model model) {
        User currentUser = authService.getCurrentUser(session);

        // Default to Demo Student if no user in session
        if (currentUser == null) {
            currentUser = authService.login("student@careerforge.ai", "student123", session);
        }

        // 1. Student Context Data
        StudentProfile studentProfile = studentProfileRepository.findByUser(currentUser)
                .orElseGet(() -> studentProfileRepository.findAll().stream().findFirst().orElse(null));

        ReadinessScoreDto readiness = placementReadinessService.calculateAndUpdateReadinessScore(studentProfile);
        SkillGapResultDto skillGap = skillGapService.analyzeSkillGap(studentProfile, studentProfile != null && studentProfile.getCareerGoal() != null ? studentProfile.getCareerGoal() : "Full Stack Developer");
        Map<String, Object> guidance = careerAdvisorService.getCareerGuidance(studentProfile);
        ResumeData resume = resumeService.getOrCreateResumeData(studentProfile);
        List<MockInterview> interviewHistory = mockInterviewService.getStudentInterviewHistory(studentProfile);
        List<JobMatchDto> recommendedJobs = jobService.getRecommendedJobsForStudent(studentProfile);

        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentProfile(studentProfile);
        List<Project> projects = projectRepository.findByStudentProfile(studentProfile);
        List<Certification> certs = certificationRepository.findByStudentProfile(studentProfile);

        // 2. Recruiter Context Data
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findAll().stream().findFirst().orElse(null);
        List<StudentProfile> allCandidates = studentProfileRepository.findAll();
        allCandidates.sort((a, b) -> Integer.compare(
                b.getReadinessScore() != null ? b.getReadinessScore() : 0,
                a.getReadinessScore() != null ? a.getReadinessScore() : 0
        ));
        List<Job> postedJobs = jobRepository.findAll();
        List<Application> allApplications = applicationRepository.findAll();

        // 3. Admin Context Data
        Map<String, Object> adminMetrics = adminAnalyticsService.getAdminDashboardMetrics();
        List<Skill> globalSkills = skillRepository.findAll();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeRole", activeRole);

        // Student Props
        model.addAttribute("studentProfile", studentProfile);
        model.addAttribute("readiness", readiness);
        model.addAttribute("skillGap", skillGap);
        model.addAttribute("guidance", guidance);
        model.addAttribute("resume", resume);
        model.addAttribute("interviewHistory", interviewHistory);
        model.addAttribute("recommendedJobs", recommendedJobs);
        model.addAttribute("studentSkills", studentSkills);
        model.addAttribute("projects", projects);
        model.addAttribute("certs", certs);

        // Recruiter Props
        model.addAttribute("recruiterProfile", recruiterProfile);
        model.addAttribute("candidates", allCandidates);
        model.addAttribute("postedJobs", postedJobs);
        model.addAttribute("allApplications", allApplications);

        // Admin Props
        model.addAttribute("adminMetrics", adminMetrics);
        model.addAttribute("globalSkills", globalSkills);

        return "landing";
    }
}
