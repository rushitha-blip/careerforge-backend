package com.careerforge.service;

import com.careerforge.dto.SkillGapResultDto;
import com.careerforge.model.Skill;
import com.careerforge.model.StudentProfile;
import com.careerforge.model.StudentSkill;
import com.careerforge.model.User;
import com.careerforge.repository.StudentSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillGapServiceTest {

    @Mock
    private StudentSkillRepository studentSkillRepository;

    @InjectMocks
    private SkillGapService skillGapService;

    private StudentProfile profile;

    @BeforeEach
    void setUp() {
        User user = new User("Priya", "priya@example.com", "pass", "STUDENT", "12345");
        profile = new StudentProfile(user, "IT", "4th Year", 9.0, "Java Developer");
    }

    @Test
    @DisplayName("Should correctly calculate skill gap percentage and missing skills")
    void testAnalyzeSkillGap() {
        // Java Developer benchmark requires: Java, Spring Boot, Spring MVC, Hibernate, MySQL, REST APIs, Git, Maven (8 skills)
        List<StudentSkill> owned = List.of(
                new StudentSkill(profile, new Skill("Java", "Technical", "High"), "ADVANCED"),
                new StudentSkill(profile, new Skill("Spring Boot", "Technical", "High"), "INTERMEDIATE"),
                new StudentSkill(profile, new Skill("MySQL", "Technical", "High"), "ADVANCED"),
                new StudentSkill(profile, new Skill("Git", "Tool", "High"), "ADVANCED")
        );
        when(studentSkillRepository.findByStudentProfile(profile)).thenReturn(owned);

        SkillGapResultDto result = skillGapService.analyzeSkillGap(profile, "Java Developer");

        assertNotNull(result);
        assertEquals("Java Developer", result.getTargetRole());
        assertEquals(4, result.getOwnedSkills().size());
        assertEquals(4, result.getMissingSkills().size());
        assertTrue(result.getMissingSkills().contains("Hibernate"));
        assertTrue(result.getMissingSkills().contains("REST APIs"));
        // 4 matched out of 8 required = 50%
        assertEquals(50, result.getMatchPercentage());
        assertFalse(result.getRecommendations().isEmpty());
    }

    @Test
    @DisplayName("Should return 100% match when student possesses all target benchmark skills")
    void testAnalyzeSkillGapFullMatch() {
        List<StudentSkill> owned = List.of(
                new StudentSkill(profile, new Skill("Java", "Technical", "High"), "ADVANCED"),
                new StudentSkill(profile, new Skill("Spring Boot", "Technical", "High"), "ADVANCED"),
                new StudentSkill(profile, new Skill("Spring MVC", "Technical", "High"), "ADVANCED"),
                new StudentSkill(profile, new Skill("Hibernate", "Technical", "High"), "ADVANCED"),
                new StudentSkill(profile, new Skill("MySQL", "Technical", "High"), "ADVANCED"),
                new StudentSkill(profile, new Skill("REST APIs", "Technical", "High"), "ADVANCED"),
                new StudentSkill(profile, new Skill("Git", "Tool", "High"), "ADVANCED"),
                new StudentSkill(profile, new Skill("Maven", "Tool", "High"), "ADVANCED")
        );
        when(studentSkillRepository.findByStudentProfile(profile)).thenReturn(owned);

        SkillGapResultDto result = skillGapService.analyzeSkillGap(profile, "Java Developer");

        assertEquals(100, result.getMatchPercentage());
        assertTrue(result.getMissingSkills().isEmpty());
    }
}
