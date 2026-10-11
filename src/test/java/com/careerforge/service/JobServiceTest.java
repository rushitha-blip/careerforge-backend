package com.careerforge.service;

import com.careerforge.dto.JobMatchDto;
import com.careerforge.model.*;
import com.careerforge.repository.ApplicationRepository;
import com.careerforge.repository.JobRepository;
import com.careerforge.repository.StudentSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private StudentSkillRepository studentSkillRepository;

    @InjectMocks
    private JobService jobService;

    private StudentProfile profile;
    private Job job;

    @BeforeEach
    void setUp() {
        User studentUser = new User("Aarav", "aarav@example.com", "pass", "STUDENT", "12345");
        profile = new StudentProfile(studentUser, "CSE", "4th Year", 8.5, "Java Developer");
        profile.setId(10L);

        User recruiterUser = new User("HR", "hr@techcorp.com", "pass", "RECRUITER", "67890");
        RecruiterProfile recruiter = new RecruiterProfile(recruiterUser, "TechCorp", "https://techcorp.com", "IT", "Bangalore");
        job = new Job(recruiter, "Java Developer", "TechCorp", "Java Developer", "Bangalore", "10 LPA", "Java, Spring Boot, MySQL", "Backend role");
        job.setId(100L);
    }

    @Test
    @DisplayName("Should recommend jobs with match percentage based on student skills")
    void testGetRecommendedJobs() {
        when(jobRepository.findByStatus("OPEN")).thenReturn(List.of(job));
        when(applicationRepository.findByStudentProfile(profile)).thenReturn(List.of());

        List<StudentSkill> owned = List.of(
                new StudentSkill(profile, new Skill("Java", "Technical", "High"), "ADVANCED"),
                new StudentSkill(profile, new Skill("MySQL", "Technical", "High"), "INTERMEDIATE")
        );
        when(studentSkillRepository.findByStudentProfile(profile)).thenReturn(owned);

        List<JobMatchDto> matches = jobService.getRecommendedJobsForStudent(profile);

        assertNotNull(matches);
        assertEquals(1, matches.size());
        JobMatchDto match = matches.get(0);
        assertEquals(job.getId(), match.getJob().getId());
        assertTrue(match.getMatchingSkills().contains("Java"));
        assertTrue(match.getMatchingSkills().contains("MySQL"));
        assertTrue(match.getMissingSkills().contains("Spring Boot"));
        // 2 matched out of 3 = 67% + 5% CGPA boost (8.5 >= 8.0) = 72%
        assertTrue(match.getMatchPercentage() >= 67);
        assertFalse(match.isApplied());
    }

    @Test
    @DisplayName("Should prevent duplicate applications and return existing application instance")
    void testApplyForJobPreventDuplicate() {
        when(jobRepository.findById(100L)).thenReturn(Optional.of(job));
        Application existing = new Application(profile, job, 85);
        when(applicationRepository.findByStudentProfileAndJob(profile, job)).thenReturn(Optional.of(existing));

        Application result = jobService.applyForJob(profile, 100L);

        assertNotNull(result);
        assertEquals(existing, result);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    @DisplayName("Should create and persist application when student applies for the first time")
    void testApplyForJobNewApplication() {
        when(jobRepository.findById(100L)).thenReturn(Optional.of(job));
        when(applicationRepository.findByStudentProfileAndJob(profile, job)).thenReturn(Optional.empty());
        when(studentSkillRepository.findByStudentProfile(profile)).thenReturn(List.of());
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));

        Application result = jobService.applyForJob(profile, 100L);

        assertNotNull(result);
        assertEquals("APPLIED", result.getStatus());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }
}
