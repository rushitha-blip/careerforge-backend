package com.careerforge.controller;

import com.careerforge.model.*;
import com.careerforge.repository.*;
import com.careerforge.service.AuthService;
import com.careerforge.service.ResumePdfService;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private ResumePdfService resumePdfService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String skillFilter,
                            @RequestParam(required = false) String branchFilter,
                            @RequestParam(required = false) Double minCgpa,
                            @RequestParam(required = false) Integer minReadiness,
                            HttpSession session, Model model) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || !"RECRUITER".equals(currentUser.getRole())) {
            return "redirect:/login";
        }

        RecruiterProfile recruiter = recruiterProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new IllegalStateException("Recruiter profile not found"));

        List<StudentProfile> candidates = studentProfileRepository.findAll();

        // Apply filters
        if (branchFilter != null && !branchFilter.isBlank()) {
            candidates = candidates.stream()
                    .filter(s -> branchFilter.equalsIgnoreCase(s.getBranch()))
                    .collect(Collectors.toList());
        }
        if (minCgpa != null) {
            candidates = candidates.stream()
                    .filter(s -> s.getCgpa() != null && s.getCgpa() >= minCgpa)
                    .collect(Collectors.toList());
        }
        if (minReadiness != null) {
            candidates = candidates.stream()
                    .filter(s -> s.getReadinessScore() != null && s.getReadinessScore() >= minReadiness)
                    .collect(Collectors.toList());
        }
        if (skillFilter != null && !skillFilter.isBlank()) {
            candidates = candidates.stream()
                    .filter(s -> {
                        List<StudentSkill> ssList = studentSkillRepository.findByStudentProfile(s);
                        return ssList.stream().anyMatch(ss -> ss.getSkill().getName().equalsIgnoreCase(skillFilter.trim()));
                    })
                    .collect(Collectors.toList());
        }

        // Candidate Ranking: Sort descending by Readiness score
        candidates.sort((a, b) -> Integer.compare(
                b.getReadinessScore() != null ? b.getReadinessScore() : 0,
                a.getReadinessScore() != null ? a.getReadinessScore() : 0
        ));

        List<Job> postedJobs = jobRepository.findByRecruiterProfile(recruiter);
        List<Application> totalApplications = applicationRepository.findAll().stream()
                .filter(app -> app.getJob().getRecruiterProfile() != null && app.getJob().getRecruiterProfile().getId().equals(recruiter.getId()))
                .collect(Collectors.toList());

        model.addAttribute("user", currentUser);
        model.addAttribute("recruiter", recruiter);
        model.addAttribute("candidates", candidates);
        model.addAttribute("postedJobs", postedJobs);
        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("selectedSkill", skillFilter);
        model.addAttribute("selectedBranch", branchFilter);
        model.addAttribute("selectedMinCgpa", minCgpa);
        model.addAttribute("selectedMinReadiness", minReadiness);

        return "recruiter-dashboard";
    }

    @PostMapping("/job/create")
    public String createJob(@RequestParam String title,
                            @RequestParam String company,
                            @RequestParam String roleCategory,
                            @RequestParam String location,
                            @RequestParam String packageOffered,
                            @RequestParam String requiredSkills,
                            @RequestParam String description,
                            HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser != null && "RECRUITER".equals(currentUser.getRole())) {
            RecruiterProfile recruiter = recruiterProfileRepository.findByUser(currentUser).orElseThrow();
            Job job = new Job(recruiter, title, company, roleCategory, location, packageOffered, requiredSkills, description);
            jobRepository.save(job);
            redirectAttributes.addFlashAttribute("successMessage", "Job opening posted successfully!");
        }
        return "redirect:/recruiter/dashboard#tab-posted-jobs";
    }

    @PostMapping("/application/status")
    public String updateApplicationStatus(@RequestParam Long appId,
                                          @RequestParam String status,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser != null && "RECRUITER".equals(currentUser.getRole())) {
            Application app = applicationRepository.findById(appId).orElseThrow();
            app.setStatus(status);
            applicationRepository.save(app);
            redirectAttributes.addFlashAttribute("successMessage", "Application status updated to: " + status);
        }
        return "redirect:/recruiter/dashboard#tab-applications";
    }

    @GetMapping("/candidate/{studentProfileId}/resume-pdf")
    public ResponseEntity<byte[]> viewCandidateResume(@PathVariable Long studentProfileId, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || (!"RECRUITER".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole()))) {
            return ResponseEntity.status(401).build();
        }

        StudentProfile profile = studentProfileRepository.findById(studentProfileId).orElseThrow();
        byte[] pdfBytes = resumePdfService.generateResumePdf(profile);

        String filename = "Candidate_" + profile.getUser().getName().replaceAll("\\s+", "_") + "_Resume.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
