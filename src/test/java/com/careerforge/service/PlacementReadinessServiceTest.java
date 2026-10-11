package com.careerforge.service;

import com.careerforge.dto.ReadinessScoreDto;
import com.careerforge.model.*;
import com.careerforge.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlacementReadinessServiceTest {

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private StudentSkillRepository studentSkillRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private MockInterviewRepository mockInterviewRepository;

    @Mock
    private ResumeDataRepository resumeDataRepository;

    @InjectMocks
    private PlacementReadinessService placementReadinessService;

    private StudentProfile profile;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Rahul Sharma", "rahul@example.com", "encodedPass", "STUDENT", "9876543210");
        user.setId(1L);
        profile = new StudentProfile(user, "Computer Science & Engg", "4th Year", 8.8, "Full Stack Developer");
        profile.setId(1L);
    }

    @Test
    @DisplayName("Should return 0 score and guidance when profile is null")
    void testCalculateScoreWithNullProfile() {
        ReadinessScoreDto result = placementReadinessService.calculateAndUpdateReadinessScore(null);
        assertNotNull(result);
        assertEquals(0, result.getTotalScore());
        assertEquals("Needs Focus", result.getStatus());
        assertFalse(result.getFeedbackItems().isEmpty());
    }

    @Test
    @DisplayName("Should compute 5-pillar score accurately with weighted breakdown (40/20/15/15/10)")
    void testCalculateReadinessScoreStandardBreakdown() {
        // Mock Skills (Max 40)
        Skill s1 = new Skill("Java", "Technical", "High");
        Skill s2 = new Skill("Spring Boot", "Technical", "High");
        List<StudentSkill> skills = List.of(
                new StudentSkill(profile, s1, "ADVANCED"),     // +8
                new StudentSkill(profile, s2, "INTERMEDIATE")  // +5
        );
        when(studentSkillRepository.findByStudentProfile(profile)).thenReturn(skills);

        // Mock Interviews (Aptitude + Interview)
        MockInterview mi = new MockInterview(profile, "TECHNICAL", 80, 80, 70, 90, "Good", "None");
        when(mockInterviewRepository.findByStudentProfileOrderByTakenAtDesc(profile)).thenReturn(List.of(mi));

        // Mock Projects (Count = 2 -> 11 pts)
        when(projectRepository.countByStudentProfile(profile)).thenReturn(2L);

        // Mock Resume Data (atsScore = 80 -> (80/100)*15 = 12 pts)
        ResumeData resume = new ResumeData();
        resume.setAtsScore(80);
        when(resumeDataRepository.findByStudentProfile(profile)).thenReturn(Optional.of(resume));

        // Mock Certifications
        when(certificationRepository.countByStudentProfile(profile)).thenReturn(1L);

        when(studentProfileRepository.save(any(StudentProfile.class))).thenReturn(profile);

        ReadinessScoreDto result = placementReadinessService.calculateAndUpdateReadinessScore(profile);

        assertNotNull(result);
        assertEquals(13, result.getTechSkillScore()); // 8 + 5
        assertEquals(18, result.getCodingAptitudeScore()); // (90 / 100) * 20 = 18
        assertEquals(11, result.getProjectScore()); // 2 projects
        assertEquals(12, result.getResumeScore()); // (80 / 100) * 15 = 12
        assertEquals(7, result.getInterviewScore()); // (70 / 100) * 10 = 7
        assertEquals(61, result.getTotalScore()); // 13 + 18 + 11 + 12 + 7 = 61
        assertEquals("High Potential", result.getStatus());

        verify(studentProfileRepository, times(1)).save(profile);
    }

    @Test
    @DisplayName("Should clamp total score to maximum 100 and set 'Placement Ready' status")
    void testCalculateScoreMaxCap() {
        // Add 10 Advanced skills (8 * 10 = 80, capped at 40)
        List<StudentSkill> skills = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            skills.add(new StudentSkill(profile, new Skill("Skill" + i, "Technical", "High"), "ADVANCED"));
        }
        when(studentSkillRepository.findByStudentProfile(profile)).thenReturn(skills);

        MockInterview mi = new MockInterview(profile, "ALL", 100, 100, 100, 100, "Superb", "None");
        when(mockInterviewRepository.findByStudentProfileOrderByTakenAtDesc(profile)).thenReturn(List.of(mi));

        when(projectRepository.countByStudentProfile(profile)).thenReturn(5L); // 15 pts

        ResumeData resume = new ResumeData();
        resume.setAtsScore(100); // 15 pts
        when(resumeDataRepository.findByStudentProfile(profile)).thenReturn(Optional.of(resume));

        when(studentProfileRepository.save(any(StudentProfile.class))).thenReturn(profile);

        ReadinessScoreDto result = placementReadinessService.calculateAndUpdateReadinessScore(profile);

        assertEquals(40, result.getTechSkillScore());
        assertEquals(20, result.getCodingAptitudeScore());
        assertEquals(15, result.getProjectScore());
        assertEquals(15, result.getResumeScore());
        assertEquals(10, result.getInterviewScore());
        assertEquals(100, result.getTotalScore());
        assertEquals("Placement Ready", result.getStatus());
    }
}
