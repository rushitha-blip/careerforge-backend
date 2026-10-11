package com.careerforge.repository;

import com.careerforge.model.Application;
import com.careerforge.model.Job;
import com.careerforge.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentProfile(StudentProfile studentProfile);
    List<Application> findByJob(Job job);
    Optional<Application> findByStudentProfileAndJob(StudentProfile studentProfile, Job job);
    long countByStudentProfile(StudentProfile studentProfile);
    long countByJob(Job job);
}
