package com.careerforge.service;

import com.careerforge.model.MockInterview;
import com.careerforge.model.MockQuestion;
import com.careerforge.model.StudentProfile;
import com.careerforge.repository.MockInterviewRepository;
import com.careerforge.repository.MockQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MockInterviewService {

    @Autowired
    private MockQuestionRepository mockQuestionRepository;

    @Autowired
    private MockInterviewRepository mockInterviewRepository;

    @Autowired
    private PlacementReadinessService placementReadinessService;

    public List<MockQuestion> getQuestionsForQuiz(String category) {
        if ("ALL".equalsIgnoreCase(category) || category == null) {
            return mockQuestionRepository.findAll();
        }
        return mockQuestionRepository.findByCategory(category.toUpperCase());
    }

    public MockInterview submitInterviewAnswers(StudentProfile profile, String category, Map<Long, String> userAnswers) {
        List<MockQuestion> questions = mockQuestionRepository.findAll();
        int total = userAnswers.size();
        if (total == 0) total = 1;

        int correctCount = 0;
        int techCorrect = 0, techTotal = 0;
        int hrCorrect = 0, hrTotal = 0;
        int aptCorrect = 0, aptTotal = 0;

        for (Map.Entry<Long, String> entry : userAnswers.entrySet()) {
            Long qId = entry.getKey();
            String ans = entry.getValue();

            Optional<MockQuestion> qOpt = mockQuestionRepository.findById(qId);
            if (qOpt.isPresent()) {
                MockQuestion q = qOpt.get();
                boolean isCorrect = q.getCorrectAnswer().equalsIgnoreCase(ans.trim());
                if (isCorrect) correctCount++;

                if ("TECHNICAL".equalsIgnoreCase(q.getCategory())) {
                    techTotal++;
                    if (isCorrect) techCorrect++;
                } else if ("HR".equalsIgnoreCase(q.getCategory())) {
                    hrTotal++;
                    if (isCorrect) hrCorrect++;
                } else {
                    aptTotal++;
                    if (isCorrect) aptCorrect++;
                }
            }
        }

        int overallScore = (int) Math.round(((double) correctCount / total) * 100);
        int techScore = techTotal > 0 ? (int) Math.round(((double) techCorrect / techTotal) * 100) : overallScore;
        int hrScore = hrTotal > 0 ? (int) Math.round(((double) hrCorrect / hrTotal) * 100) : overallScore;
        int aptScore = aptTotal > 0 ? (int) Math.round(((double) aptCorrect / aptTotal) * 100) : overallScore;

        List<String> weakList = new ArrayList<>();
        if (techScore < 70) weakList.add("Java Core & Spring Boot Architecture");
        if (hrScore < 70) weakList.add("Behavioral STAR Method & Communication");
        if (aptScore < 70) weakList.add("Quantitative Aptitude & Logical Reasoning");
        String weakAreas = weakList.isEmpty() ? "None! Strong across all competencies." : String.join(", ", weakList);

        String feedback = overallScore >= 80 ? "Exceptional performance! Demonstrates strong problem-solving skills and domain knowledge."
                : overallScore >= 60 ? "Good overall foundation. Focus on refining weak areas identified above before live campus drives."
                : "Additional practice recommended. Review Java core concepts and aptitude fundamentals.";

        MockInterview interview = new MockInterview(profile, category, overallScore, techScore, hrScore, aptScore, feedback, weakAreas);
        MockInterview saved = mockInterviewRepository.save(interview);

        // Trigger placement readiness score recalculation
        placementReadinessService.calculateAndUpdateReadinessScore(profile);

        return saved;
    }

    public List<MockInterview> getStudentInterviewHistory(StudentProfile profile) {
        return mockInterviewRepository.findByStudentProfileOrderByTakenAtDesc(profile);
    }
}
