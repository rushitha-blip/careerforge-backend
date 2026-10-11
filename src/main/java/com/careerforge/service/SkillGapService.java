package com.careerforge.service;

import com.careerforge.dto.SkillGapResultDto;
import com.careerforge.model.StudentProfile;
import com.careerforge.model.StudentSkill;
import com.careerforge.repository.StudentSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillGapService {

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    private static final Map<String, List<String>> ROLE_SKILLS_MAP = new HashMap<>();

    static {
        ROLE_SKILLS_MAP.put("Software Engineer", Arrays.asList("Java", "Data Structures", "Algorithms", "SQL", "Git", "System Design", "HTML", "CSS"));
        ROLE_SKILLS_MAP.put("Java Developer", Arrays.asList("Java", "Spring Boot", "Spring MVC", "Hibernate", "MySQL", "REST APIs", "Git", "Maven"));
        ROLE_SKILLS_MAP.put("Full Stack Developer", Arrays.asList("Java", "Spring Boot", "MySQL", "HTML5", "CSS3", "JavaScript", "Bootstrap", "REST APIs", "React", "Git"));
        ROLE_SKILLS_MAP.put("Backend Developer", Arrays.asList("Java", "Spring Boot", "Microservices", "MySQL", "PostgreSQL", "Redis", "Docker", "REST APIs", "Git"));
        ROLE_SKILLS_MAP.put("Data Scientist", Arrays.asList("Python", "SQL", "Machine Learning", "Pandas", "NumPy", "Data Visualization", "Statistics", "Git"));
        ROLE_SKILLS_MAP.put("AI Engineer", Arrays.asList("Python", "TensorFlow", "PyTorch", "Deep Learning", "NLP", "Computer Vision", "REST APIs", "Docker"));
    }

    public SkillGapResultDto analyzeSkillGap(StudentProfile profile, String targetRole) {
        String roleKey = targetRole;
        if (!ROLE_SKILLS_MAP.containsKey(roleKey)) {
            roleKey = "Full Stack Developer"; // Default fallback
        }

        List<String> requiredSkills = ROLE_SKILLS_MAP.get(roleKey);
        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentProfile(profile);

        List<String> ownedSkills = studentSkills.stream()
                .map(ss -> ss.getSkill().getName())
                .collect(Collectors.toList());

        List<String> missingSkills = new ArrayList<>();
        int matchedCount = 0;

        for (String req : requiredSkills) {
            boolean matched = ownedSkills.stream().anyMatch(s -> s.equalsIgnoreCase(req));
            if (matched) {
                matchedCount++;
            } else {
                missingSkills.add(req);
            }
        }

        int matchPercentage = (int) Math.round(((double) matchedCount / requiredSkills.size()) * 100);

        List<String> recs = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            recs.add("Focus on learning core prerequisite: " + missingSkills.get(0));
            if (missingSkills.size() > 1) {
                recs.add("Build a mini project demonstrating " + missingSkills.get(1));
            }
            recs.add("Complete online certifications in " + String.join(", ", missingSkills));
        } else {
            recs.add("Exemplary skill match! Proceed to take Mock Interviews and apply to target jobs.");
        }

        return new SkillGapResultDto(targetRole, ownedSkills, requiredSkills, missingSkills, matchPercentage, recs);
    }

    public Map<String, List<String>> getAllRoleSkillsMap() {
        return ROLE_SKILLS_MAP;
    }
}
