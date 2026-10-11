package com.careerforge.service;

import com.careerforge.model.*;
import com.careerforge.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    @Autowired
    private PlacementReadinessService placementReadinessService;

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerStudent(String name, String email, String password, String phone, String branch, String year, Double cgpa) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered!");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(name, email, hashedPassword, "STUDENT", phone);
        User savedUser = userRepository.save(user);

        StudentProfile profile = new StudentProfile(savedUser, branch, year, cgpa != null ? cgpa : 8.0, "Full Stack Developer");
        StudentProfile savedProfile = studentProfileRepository.save(profile);

        // Generate baseline personalized roadmap
        roadmapService.getOrCreateRoadmapForStudent(savedProfile);

        placementReadinessService.calculateAndUpdateReadinessScore(savedProfile);

        return savedUser;
    }

    public User registerRecruiter(String name, String email, String password, String phone, String companyName, String companyWebsite, String industry, String location) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered!");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(name, email, hashedPassword, "RECRUITER", phone);
        User savedUser = userRepository.save(user);

        RecruiterProfile profile = new RecruiterProfile(savedUser, companyName, companyWebsite, industry, location);
        recruiterProfileRepository.save(profile);

        return savedUser;
    }

    public User login(String email, String password, HttpSession session) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean matches = passwordEncoder.matches(password, user.getPassword()) || user.getPassword().equals(password);
            if (matches) {
                // Update to hashed password if it was plain text
                if (user.getPassword().equals(password)) {
                    user.setPassword(passwordEncoder.encode(password));
                    userRepository.save(user);
                }

                // Set Spring Security context
                String roleName = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(roleName))
                );
                org.springframework.security.core.context.SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);

                session.setAttribute("SPRING_SECURITY_CONTEXT", context);
                session.setAttribute("currentUser", user);
                return user;
            }
        }
        throw new IllegalArgumentException("Invalid email or password");
    }

    public User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            return user;
        }

        // Try resolving from Spring Security Context
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            String principalEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> uOpt = userRepository.findByEmail(principalEmail);
            if (uOpt.isPresent()) {
                session.setAttribute("currentUser", uOpt.get());
                return uOpt.get();
            }
        }

        return null;
    }

    public void logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.removeAttribute("currentUser");
        session.invalidate();
    }
}
