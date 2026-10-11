package com.careerforge.repository;

import com.careerforge.model.Job;
import com.careerforge.model.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(String status);
    List<Job> findByRecruiterProfile(RecruiterProfile recruiterProfile);
    List<Job> findByRoleCategory(String roleCategory);
}
