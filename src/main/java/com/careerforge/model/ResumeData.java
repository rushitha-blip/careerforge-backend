package com.careerforge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "resume_data")
public class ResumeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @Column(length = 2000)
    private String summary;

    private Integer atsScore = 0; // Out of 100
    private String templateName = "Modern Standard";

    @Column(length = 1000)
    private String skillsCsv;

    @Column(length = 4000)
    private String experienceJson;

    @Column(length = 4000)
    private String educationJson;

    @Column(length = 4000)
    private String projectsJson;

    @Column(length = 2000)
    private String atsSuggestions;

    public ResumeData() {}

    public ResumeData(StudentProfile studentProfile, String summary, Integer atsScore, String templateName, String skillsCsv, String experienceJson, String educationJson, String projectsJson, String atsSuggestions) {
        this.studentProfile = studentProfile;
        this.summary = summary;
        this.atsScore = atsScore;
        this.templateName = templateName;
        this.skillsCsv = skillsCsv;
        this.experienceJson = experienceJson;
        this.educationJson = educationJson;
        this.projectsJson = projectsJson;
        this.atsSuggestions = atsSuggestions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudentProfile() { return studentProfile; }
    public void setStudentProfile(StudentProfile studentProfile) { this.studentProfile = studentProfile; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Integer getAtsScore() { return atsScore; }
    public void setAtsScore(Integer atsScore) { this.atsScore = atsScore; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getSkillsCsv() { return skillsCsv; }
    public void setSkillsCsv(String skillsCsv) { this.skillsCsv = skillsCsv; }

    public String getExperienceJson() { return experienceJson; }
    public void setExperienceJson(String experienceJson) { this.experienceJson = experienceJson; }

    public String getEducationJson() { return educationJson; }
    public void setEducationJson(String educationJson) { this.educationJson = educationJson; }

    public String getProjectsJson() { return projectsJson; }
    public void setProjectsJson(String projectsJson) { this.projectsJson = projectsJson; }

    public String getAtsSuggestions() { return atsSuggestions; }
    public void setAtsSuggestions(String atsSuggestions) { this.atsSuggestions = atsSuggestions; }
}
