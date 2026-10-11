package com.careerforge.dto;

import java.util.List;

public class SkillGapResultDto {
    private String targetRole;
    private List<String> studentSkills;
    private List<String> requiredSkills;
    private List<String> missingSkills;
    private int matchPercentage;
    private List<String> recommendations;

    public SkillGapResultDto() {}

    public SkillGapResultDto(String targetRole, List<String> studentSkills, List<String> requiredSkills, List<String> missingSkills, int matchPercentage, List<String> recommendations) {
        this.targetRole = targetRole;
        this.studentSkills = studentSkills;
        this.requiredSkills = requiredSkills;
        this.missingSkills = missingSkills;
        this.matchPercentage = matchPercentage;
        this.recommendations = recommendations;
    }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public List<String> getStudentSkills() { return studentSkills; }
    public List<String> getOwnedSkills() { return studentSkills; }
    public void setStudentSkills(List<String> studentSkills) { this.studentSkills = studentSkills; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) { this.matchPercentage = matchPercentage; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}
