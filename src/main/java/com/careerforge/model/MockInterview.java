package com.careerforge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mock_interviews")
public class MockInterview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    private String category; // TECHNICAL, HR, APTITUDE, COMPREHENSIVE
    private Integer overallScore = 0; // %
    private Integer technicalScore = 0;
    private Integer hrScore = 0;
    private Integer aptitudeScore = 0;

    @Column(length = 1000)
    private String feedback;

    @Column(length = 1000)
    private String weakAreas;

    private LocalDateTime takenAt = LocalDateTime.now();

    public MockInterview() {}

    public MockInterview(StudentProfile studentProfile, String category, Integer overallScore, Integer technicalScore, Integer hrScore, Integer aptitudeScore, String feedback, String weakAreas) {
        this.studentProfile = studentProfile;
        this.category = category;
        this.overallScore = overallScore;
        this.technicalScore = technicalScore;
        this.hrScore = hrScore;
        this.aptitudeScore = aptitudeScore;
        this.feedback = feedback;
        this.weakAreas = weakAreas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudentProfile() { return studentProfile; }
    public void setStudentProfile(StudentProfile studentProfile) { this.studentProfile = studentProfile; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public Integer getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(Integer technicalScore) { this.technicalScore = technicalScore; }

    public Integer getHrScore() { return hrScore; }
    public void setHrScore(Integer hrScore) { this.hrScore = hrScore; }

    public Integer getAptitudeScore() { return aptitudeScore; }
    public void setAptitudeScore(Integer aptitudeScore) { this.aptitudeScore = aptitudeScore; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getWeakAreas() { return weakAreas; }
    public void setWeakAreas(String weakAreas) { this.weakAreas = weakAreas; }

    public LocalDateTime getTakenAt() { return takenAt; }
    public void setTakenAt(LocalDateTime takenAt) { this.takenAt = takenAt; }
}
