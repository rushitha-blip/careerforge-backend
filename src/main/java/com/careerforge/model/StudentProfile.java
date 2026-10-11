package com.careerforge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String branch;
    @Column(name = "academic_year")
    private String year;
    private Double cgpa;
    private String careerGoal; // e.g. Software Engineer, Full Stack Developer
    private String linkedinUrl;
    private String githubUrl;

    @Column(length = 1000)
    private String bio;

    // Placement Readiness Scores
    private Integer readinessScore = 0; // Out of 100
    private Integer techSkillScore = 0; // Max 40
    private Integer codingAptitudeScore = 0; // Max 20
    private Integer projectScore = 0;   // Max 15
    private Integer resumeScore = 0;    // Max 15
    private Integer certScore = 0;      // Additional credential support
    private Integer interviewScore = 0; // Max 10

    public StudentProfile() {}

    public StudentProfile(User user, String branch, String year, Double cgpa, String careerGoal) {
        this.user = user;
        this.branch = branch;
        this.year = year;
        this.cgpa = cgpa;
        this.careerGoal = careerGoal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public String getCareerGoal() { return careerGoal; }
    public void setCareerGoal(String careerGoal) { this.careerGoal = careerGoal; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Integer getReadinessScore() { return readinessScore; }
    public void setReadinessScore(Integer readinessScore) { this.readinessScore = readinessScore; }

    public Integer getTechSkillScore() { return techSkillScore; }
    public void setTechSkillScore(Integer techSkillScore) { this.techSkillScore = techSkillScore; }

    public Integer getProjectScore() { return projectScore; }
    public void setProjectScore(Integer projectScore) { this.projectScore = projectScore; }

    public Integer getResumeScore() { return resumeScore; }
    public void setResumeScore(Integer resumeScore) { this.resumeScore = resumeScore; }

    public Integer getCertScore() { return certScore; }
    public void setCertScore(Integer certScore) { this.certScore = certScore; }

    public Integer getCodingAptitudeScore() { return codingAptitudeScore; }
    public void setCodingAptitudeScore(Integer codingAptitudeScore) { this.codingAptitudeScore = codingAptitudeScore; }

    public Integer getInterviewScore() { return interviewScore; }
    public void setInterviewScore(Integer interviewScore) { this.interviewScore = interviewScore; }
}
