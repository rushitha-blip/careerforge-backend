package com.careerforge.controller;

import com.careerforge.dto.JobMatchDto;
import com.careerforge.dto.ReadinessScoreDto;
import com.careerforge.dto.SkillGapResultDto;
import com.careerforge.model.*;
import com.careerforge.repository.*;
import com.careerforge.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private AuthService authService;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private PlacementReadinessService placementReadinessService;

    @Autowired
    private SkillGapService skillGapService;

    @Autowired
    private CareerAdvisorService careerAdvisorService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ResumePdfService resumePdfService;

    @Autowired
    private MockInterviewService mockInterviewService;

    @Autowired
    private JobService jobService;

    @Autowired
    private RoadmapService roadmapService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || !"STUDENT".equals(currentUser.getRole())) {
            return "redirect:/login";
        }

        StudentProfile profile = studentProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));

        ReadinessScoreDto readiness = placementReadinessService.calculateAndUpdateReadinessScore(profile);
        SkillGapResultDto skillGap = skillGapService.analyzeSkillGap(profile, profile.getCareerGoal() != null ? profile.getCareerGoal() : "Full Stack Developer");
        Map<String, Object> guidance = careerAdvisorService.getCareerGuidance(profile);
        ResumeData resume = resumeService.getOrCreateResumeData(profile);
        List<MockInterview> interviewHistory = mockInterviewService.getStudentInterviewHistory(profile);
        List<JobMatchDto> recommendedJobs = jobService.getRecommendedJobsForStudent(profile);

        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentProfile(profile);
        List<Project> projects = projectRepository.findByStudentProfile(profile);
        List<Certification> certs = certificationRepository.findByStudentProfile(profile);
        List<Skill> allSkillsCatalog = skillRepository.findAll();

        List<RoadmapTask> roadmapTasks = roadmapService.getOrCreateRoadmapForStudent(profile);
        int roadmapProgress = roadmapService.getRoadmapProgressPercentage(profile);

        model.addAttribute("user", currentUser);
        model.addAttribute("profile", profile);
        model.addAttribute("readiness", readiness);
        model.addAttribute("skillGap", skillGap);
        model.addAttribute("guidance", guidance);
        model.addAttribute("resume", resume);
        model.addAttribute("interviewHistory", interviewHistory);
        model.addAttribute("recommendedJobs", recommendedJobs);
        model.addAttribute("studentSkills", studentSkills);
        model.addAttribute("projects", projects);
        model.addAttribute("certs", certs);
        model.addAttribute("allSkillsCatalog", allSkillsCatalog);
        model.addAttribute("roadmapTasks", roadmapTasks);
        model.addAttribute("roadmapProgress", roadmapProgress);

        return "student-dashboard";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String branch,
                                @RequestParam String year,
                                @RequestParam Double cgpa,
                                @RequestParam String careerGoal,
                                @RequestParam(required = false) String bio,
                                @RequestParam(required = false) String githubUrl,
                                @RequestParam(required = false) String linkedinUrl,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser != null && "STUDENT".equals(currentUser.getRole())) {
            StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();
            profile.setBranch(branch);
            profile.setYear(year);
            profile.setCgpa(cgpa);
            profile.setCareerGoal(careerGoal);
            profile.setBio(bio);
            profile.setGithubUrl(githubUrl);
            profile.setLinkedinUrl(linkedinUrl);
            studentProfileRepository.save(profile);

            placementReadinessService.calculateAndUpdateReadinessScore(profile);
            redirectAttributes.addFlashAttribute("successMessage", "Profile details updated successfully!");
        }
        return "redirect:/student/dashboard#tab-overview";
    }

    @PostMapping("/skill/add")
    public String addSkill(@RequestParam Long skillId,
                           @RequestParam String proficiency,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser != null && "STUDENT".equals(currentUser.getRole())) {
            StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();
            Skill skill = skillRepository.findById(skillId).orElseThrow();

            Optional<StudentSkill> existing = studentSkillRepository.findByStudentProfileAndSkill(profile, skill);
            if (existing.isPresent()) {
                StudentSkill ss = existing.get();
                ss.setProficiency(proficiency);
                studentSkillRepository.save(ss);
            } else {
                StudentSkill ss = new StudentSkill(profile, skill, proficiency);
                studentSkillRepository.save(ss);
            }

            placementReadinessService.calculateAndUpdateReadinessScore(profile);
            redirectAttributes.addFlashAttribute("successMessage", "Skill '" + skill.getName() + "' saved to your profile!");
        }
        return "redirect:/student/dashboard#tab-overview";
    }

    @PostMapping("/project/add")
    public String addProject(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String techStack,
                             @RequestParam(required = false) String githubUrl,
                             @RequestParam(required = false) String liveDemoUrl,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser != null && "STUDENT".equals(currentUser.getRole())) {
            StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();
            Project p = new Project(profile, title, description, techStack, githubUrl, liveDemoUrl);
            projectRepository.save(p);

            placementReadinessService.calculateAndUpdateReadinessScore(profile);
            redirectAttributes.addFlashAttribute("successMessage", "Project '" + title + "' added to portfolio!");
        }
        return "redirect:/student/dashboard#tab-overview";
    }

    @PostMapping("/cert/add")
    public String addCertification(@RequestParam String title,
                                   @RequestParam String issuingOrganization,
                                   @RequestParam String issueDate,
                                   @RequestParam(required = false) String credentialUrl,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser != null && "STUDENT".equals(currentUser.getRole())) {
            StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();
            Certification c = new Certification(profile, title, issuingOrganization, issueDate, credentialUrl);
            certificationRepository.save(c);

            placementReadinessService.calculateAndUpdateReadinessScore(profile);
            redirectAttributes.addFlashAttribute("successMessage", "Certification added successfully!");
        }
        return "redirect:/student/dashboard#tab-overview";
    }

    @PostMapping("/job/apply/{jobId}")
    public String applyJob(@PathVariable Long jobId, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser != null && "STUDENT".equals(currentUser.getRole())) {
            StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();
            Application app = jobService.applyForJob(profile, jobId);
            if (app != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Application submitted successfully for " + app.getJob().getTitle() + "!");
            }
        }
        return "redirect:/student/dashboard#tab-jobs";
    }

    @PostMapping("/roadmap/toggle/{taskId}")
    public String toggleRoadmapTask(@PathVariable Long taskId, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser != null && "STUDENT".equals(currentUser.getRole())) {
            StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();
            RoadmapTask task = roadmapService.toggleTaskCompletion(taskId, profile);
            redirectAttributes.addFlashAttribute("successMessage", "Task updated: " + task.getTitle() + " (" + (task.isCompleted() ? "Completed" : "Pending") + ")");
        }
        return "redirect:/student/dashboard#tab-roadmap";
    }

    @GetMapping("/resume/download-pdf")
    public ResponseEntity<byte[]> downloadResumePdf(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || !"STUDENT".equals(currentUser.getRole())) {
            return ResponseEntity.status(401).build();
        }

        StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();
        byte[] pdfBytes = resumePdfService.generateResumePdf(profile);

        String filename = "Resume_" + currentUser.getName().replaceAll("\\s+", "_") + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
