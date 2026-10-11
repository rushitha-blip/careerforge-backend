package com.careerforge.controller;

import com.careerforge.dto.ReadinessScoreDto;
import com.careerforge.dto.SkillGapResultDto;
import com.careerforge.model.*;
import com.careerforge.repository.StudentProfileRepository;
import com.careerforge.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private AuthService authService;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private SkillGapService skillGapService;

    @Autowired
    private PlacementReadinessService placementReadinessService;

    @Autowired
    private MockInterviewService mockInterviewService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @Autowired
    private RoadmapService roadmapService;

    @GetMapping("/student/skillgap")
    public ResponseEntity<?> getSkillGap(@RequestParam(defaultValue = "Full Stack Developer") String role, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || !"STUDENT".equals(currentUser.getRole())) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();
        SkillGapResultDto result = skillGapService.analyzeSkillGap(profile, role);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/student/readiness")
    public ResponseEntity<?> getReadiness(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || !"STUDENT".equals(currentUser.getRole())) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();
        ReadinessScoreDto readiness = placementReadinessService.calculateAndUpdateReadinessScore(profile);
        return ResponseEntity.ok(readiness);
    }

    @GetMapping("/student/interview/questions")
    public ResponseEntity<?> getInterviewQuestions(@RequestParam(defaultValue = "ALL") String category) {
        List<MockQuestion> questions = mockInterviewService.getQuestionsForQuiz(category);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/student/interview/submit")
    public ResponseEntity<?> submitInterview(@RequestBody Map<String, Object> body, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || !"STUDENT".equals(currentUser.getRole())) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();

        String category = (String) body.getOrDefault("category", "TECHNICAL");
        Map<String, String> rawAnswers = (Map<String, String>) body.get("answers");

        Map<Long, String> answers = new HashMap<>();
        if (rawAnswers != null) {
            for (Map.Entry<String, String> e : rawAnswers.entrySet()) {
                answers.put(Long.parseLong(e.getKey()), e.getValue());
            }
        }

        MockInterview result = mockInterviewService.submitInterviewAnswers(profile, category, answers);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/student/resume/evaluate")
    public ResponseEntity<?> evaluateResume(@RequestBody Map<String, String> body, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || !"STUDENT".equals(currentUser.getRole())) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();

        String summary = body.get("summary");
        String skillsCsv = body.get("skillsCsv");
        String templateName = body.get("templateName");

        ResumeData updated = resumeService.updateAndEvaluateResume(profile, summary, skillsCsv, templateName);
        placementReadinessService.calculateAndUpdateReadinessScore(profile);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/student/roadmap/toggle/{taskId}")
    public ResponseEntity<?> toggleRoadmapTask(@PathVariable Long taskId, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || !"STUDENT".equals(currentUser.getRole())) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        StudentProfile profile = studentProfileRepository.findByUser(currentUser).orElseThrow();
        RoadmapTask task = roadmapService.toggleTaskCompletion(taskId, profile);
        int progress = roadmapService.getRoadmapProgressPercentage(profile);

        return ResponseEntity.ok(Map.of(
                "taskId", task.getId(),
                "completed", task.isCompleted(),
                "progress", progress
        ));
    }

    @GetMapping("/admin/metrics")
    public ResponseEntity<?> getAdminMetrics(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        Map<String, Object> metrics = adminAnalyticsService.getAdminDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }
}
