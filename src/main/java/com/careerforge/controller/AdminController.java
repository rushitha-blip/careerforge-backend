package com.careerforge.controller;

import com.careerforge.model.*;
import com.careerforge.repository.*;
import com.careerforge.service.AdminAnalyticsService;
import com.careerforge.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MockQuestionRepository mockQuestionRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/login";
        }

        Map<String, Object> metrics = adminAnalyticsService.getAdminDashboardMetrics();
        List<User> users = userRepository.findAll();
        List<StudentProfile> students = studentProfileRepository.findAll();
        List<RecruiterProfile> recruiters = recruiterProfileRepository.findAll();
        List<Skill> skills = skillRepository.findAll();
        List<Job> jobs = jobRepository.findAll();
        List<Application> applications = applicationRepository.findAll();
        List<MockQuestion> questions = mockQuestionRepository.findAll();

        model.addAttribute("user", currentUser);
        model.addAttribute("metrics", metrics);
        model.addAttribute("users", users);
        model.addAttribute("students", students);
        model.addAttribute("recruiters", recruiters);
        model.addAttribute("skills", skills);
        model.addAttribute("jobs", jobs);
        model.addAttribute("applications", applications);
        model.addAttribute("questions", questions);

        return "admin-dashboard";
    }

    @PostMapping("/skill/add")
    public String addSkill(@RequestParam String name,
                           @RequestParam String category,
                           @RequestParam String demandLevel,
                           RedirectAttributes redirectAttributes) {
        if (!skillRepository.findByNameIgnoreCase(name).isPresent()) {
            Skill skill = new Skill(name, category, demandLevel);
            skillRepository.save(skill);
            redirectAttributes.addFlashAttribute("successMessage", "Skill '" + name + "' added to global taxonomy!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Skill '" + name + "' already exists in catalog.");
        }
        return "redirect:/admin/dashboard#tab-admin-skills";
    }

    @PostMapping("/question/add")
    public String addQuestion(@RequestParam String category,
                             @RequestParam String topic,
                             @RequestParam String difficulty,
                             @RequestParam String question,
                             @RequestParam String optionA,
                             @RequestParam String optionB,
                             @RequestParam String optionC,
                             @RequestParam String optionD,
                             @RequestParam String correctAnswer,
                             @RequestParam String explanation,
                             RedirectAttributes redirectAttributes) {
        MockQuestion q = new MockQuestion(category, topic, difficulty, question, optionA, optionB, optionC, optionD, correctAnswer, explanation);
        mockQuestionRepository.save(q);
        redirectAttributes.addFlashAttribute("successMessage", "New question added to institutional question bank!");
        return "redirect:/admin/dashboard#tab-admin-questions";
    }
}
