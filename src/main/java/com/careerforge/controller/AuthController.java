package com.careerforge.controller;

import com.careerforge.model.User;
import com.careerforge.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user != null) {
            return redirectUser(user);
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = authService.login(email, password, session);
            return redirectUser(user);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/demo-login/{role}")
    public String demoLogin(@PathVariable String role, HttpSession session, RedirectAttributes redirectAttributes) {
        String email = "student@careerforge.ai";
        String pass = "student123";

        if ("recruiter".equalsIgnoreCase(role)) {
            email = "recruiter@careerforge.ai";
            pass = "recruiter123";
        } else if ("admin".equalsIgnoreCase(role)) {
            email = "admin@careerforge.ai";
            pass = "admin123";
        }

        try {
            User user = authService.login(email, pass, session);
            redirectAttributes.addFlashAttribute("successMessage", "Welcome, " + user.getName() + "!");
            return redirectUser(user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Demo login failed: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/register/student")
    public String showStudentRegisterPage() {
        return "register-student";
    }

    @PostMapping("/register/student")
    public String processStudentRegister(@RequestParam String name,
                                         @RequestParam String email,
                                         @RequestParam String password,
                                         @RequestParam(required = false) String phone,
                                         @RequestParam String branch,
                                         @RequestParam String year,
                                         @RequestParam(required = false) Double cgpa,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        try {
            User user = authService.registerStudent(name, email, password, phone, branch, year, cgpa);
            authService.login(email, password, session);
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully! Welcome to CareerForge AI.");
            return "redirect:/student/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register/student";
        }
    }

    @GetMapping("/register/recruiter")
    public String showRecruiterRegisterPage() {
        return "register-recruiter";
    }

    @PostMapping("/register/recruiter")
    public String processRecruiterRegister(@RequestParam String name,
                                           @RequestParam String email,
                                           @RequestParam String password,
                                           @RequestParam(required = false) String phone,
                                           @RequestParam String companyName,
                                           @RequestParam(required = false) String companyWebsite,
                                           @RequestParam(required = false) String industry,
                                           @RequestParam(required = false) String location,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        try {
            User user = authService.registerRecruiter(name, email, password, phone, companyName, companyWebsite, industry, location);
            authService.login(email, password, session);
            redirectAttributes.addFlashAttribute("successMessage", "Recruiter registered successfully!");
            return "redirect:/recruiter/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register/recruiter";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/?loggedOut=true";
    }

    private String redirectUser(User user) {
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin/dashboard";
        } else if ("RECRUITER".equals(user.getRole())) {
            return "redirect:/recruiter/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }
}
