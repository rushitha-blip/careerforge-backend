package com.careerforge.service;

import com.careerforge.model.StudentProfile;
import com.careerforge.model.StudentSkill;
import com.careerforge.repository.StudentSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CareerAdvisorService {

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    public Map<String, Object> getCareerGuidance(StudentProfile profile) {
        Map<String, Object> response = new HashMap<>();

        List<StudentSkill> skills = studentSkillRepository.findByStudentProfile(profile);
        List<String> skillNames = new ArrayList<>();
        for (StudentSkill ss : skills) {
            skillNames.add(ss.getSkill().getName());
        }

        List<Map<String, String>> recommendedRoles = new ArrayList<>();

        if (skillNames.contains("Java") || skillNames.contains("Spring Boot")) {
            Map<String, String> r1 = new HashMap<>();
            r1.put("title", "Java Full Stack Developer");
            r1.put("demand", "High (85,000+ jobs)");
            r1.put("matchScore", "88%");
            r1.put("avgSalary", "₹6 - ₹14 LPA");
            recommendedRoles.add(r1);

            Map<String, String> r2 = new HashMap<>();
            r2.put("title", "Backend Engineer (Java)");
            r2.put("demand", "Very High");
            r2.put("matchScore", "82%");
            r2.put("avgSalary", "₹8 - ₹16 LPA");
            recommendedRoles.add(r2);
        } else {
            Map<String, String> r1 = new HashMap<>();
            r1.put("title", "Software Engineer Trainee");
            r1.put("demand", "High");
            r1.put("matchScore", "75%");
            r1.put("avgSalary", "₹4.5 - ₹8 LPA");
            recommendedRoles.add(r1);
        }

        List<String> suggestedTech = Arrays.asList("Spring Boot 3", "Microservices Architecture", "Docker & Kubernetes", "MySQL Optimization", "React.js");
        List<String> suggestedCerts = Arrays.asList("Oracle Certified Professional: Java SE 17", "AWS Certified Developer Associate", "Spring Certified Professional");

        List<Map<String, String>> learningResources = new ArrayList<>();
        Map<String, String> res1 = new HashMap<>();
        res1.put("title", "Spring Boot 3 Masterclass");
        res1.put("platform", "Coursera / Udemy");
        res1.put("type", "Video Course");
        learningResources.add(res1);

        Map<String, String> res2 = new HashMap<>();
        res2.put("title", "LeetCode 75 Technical Interview Prep");
        res2.put("platform", "LeetCode");
        res2.put("type", "Coding Practice");
        learningResources.add(res2);

        response.put("recommendedRoles", recommendedRoles);
        response.put("suggestedTech", suggestedTech);
        response.put("suggestedCerts", suggestedCerts);
        response.put("learningResources", learningResources);

        return response;
    }
}
