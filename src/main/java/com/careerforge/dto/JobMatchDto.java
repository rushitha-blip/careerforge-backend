package com.careerforge.dto;

import com.careerforge.model.Job;
import java.util.List;

public class JobMatchDto {
    private Job job;
    private int matchPercentage;
    private List<String> matchingSkills;
    private List<String> missingSkills;
    private boolean applied;

    public JobMatchDto() {}

    public JobMatchDto(Job job, int matchPercentage, List<String> matchingSkills, List<String> missingSkills, boolean applied) {
        this.job = job;
        this.matchPercentage = matchPercentage;
        this.matchingSkills = matchingSkills;
        this.missingSkills = missingSkills;
        this.applied = applied;
    }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) { this.matchPercentage = matchPercentage; }

    public List<String> getMatchingSkills() { return matchingSkills; }
    public void setMatchingSkills(List<String> matchingSkills) { this.matchingSkills = matchingSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public boolean isApplied() { return applied; }
    public void setApplied(boolean applied) { this.applied = applied; }
}
