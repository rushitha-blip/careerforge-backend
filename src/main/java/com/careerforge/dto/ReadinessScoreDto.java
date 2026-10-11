package com.careerforge.dto;

import java.util.List;

public class ReadinessScoreDto {
    private int totalScore;
    private int techSkillScore;      // Max 40 (40%)
    private int codingAptitudeScore; // Max 20 (20%)
    private int projectScore;        // Max 15 (15%)
    private int resumeScore;         // Max 15 (15%)
    private int interviewScore;      // Max 10 (10%)
    private int certScore;           // Supporting credentials count/score
    private String status;           // Placement Ready, High Potential, Needs Focus
    private List<String> feedbackItems;

    public ReadinessScoreDto() {}

    public ReadinessScoreDto(int totalScore, int techSkillScore, int codingAptitudeScore, int projectScore, int resumeScore, int certScore, int interviewScore, String status, List<String> feedbackItems) {
        this.totalScore = totalScore;
        this.techSkillScore = techSkillScore;
        this.codingAptitudeScore = codingAptitudeScore;
        this.projectScore = projectScore;
        this.resumeScore = resumeScore;
        this.certScore = certScore;
        this.interviewScore = interviewScore;
        this.status = status;
        this.feedbackItems = feedbackItems;
    }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public int getTechSkillScore() { return techSkillScore; }
    public void setTechSkillScore(int techSkillScore) { this.techSkillScore = techSkillScore; }

    public int getCodingAptitudeScore() { return codingAptitudeScore; }
    public void setCodingAptitudeScore(int codingAptitudeScore) { this.codingAptitudeScore = codingAptitudeScore; }

    public int getProjectScore() { return projectScore; }
    public void setProjectScore(int projectScore) { this.projectScore = projectScore; }

    public int getResumeScore() { return resumeScore; }
    public void setResumeScore(int resumeScore) { this.resumeScore = resumeScore; }

    public int getCertScore() { return certScore; }
    public void setCertScore(int certScore) { this.certScore = certScore; }

    public int getInterviewScore() { return interviewScore; }
    public void setInterviewScore(int interviewScore) { this.interviewScore = interviewScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getFeedbackItems() { return feedbackItems; }
    public void setFeedbackItems(List<String> feedbackItems) { this.feedbackItems = feedbackItems; }
}
