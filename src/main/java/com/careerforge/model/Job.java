package com.careerforge.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recruiter_profile_id")
    private RecruiterProfile recruiterProfile;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    private String roleCategory; // Software Engineer, Data Scientist, etc.
    private String location;
    private String packageOffered; // e.g. ₹12 - ₹15 LPA
    
    @Column(length = 1000)
    private String requiredSkills; // Comma separated

    @Column(length = 3000)
    private String description;

    private LocalDate postedDate = LocalDate.now();
    private String status = "OPEN"; // OPEN, CLOSED

    public Job() {}

    public Job(RecruiterProfile recruiterProfile, String title, String company, String roleCategory, String location, String packageOffered, String requiredSkills, String description) {
        this.recruiterProfile = recruiterProfile;
        this.title = title;
        this.company = company;
        this.roleCategory = roleCategory;
        this.location = location;
        this.packageOffered = packageOffered;
        this.requiredSkills = requiredSkills;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RecruiterProfile getRecruiterProfile() { return recruiterProfile; }
    public void setRecruiterProfile(RecruiterProfile recruiterProfile) { this.recruiterProfile = recruiterProfile; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getRoleCategory() { return roleCategory; }
    public void setRoleCategory(String roleCategory) { this.roleCategory = roleCategory; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPackageOffered() { return packageOffered; }
    public void setPackageOffered(String packageOffered) { this.packageOffered = packageOffered; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDate postedDate) { this.postedDate = postedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
