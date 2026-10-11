package com.careerforge.repository;

import com.careerforge.model.Project;
import com.careerforge.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStudentProfile(StudentProfile studentProfile);
    long countByStudentProfile(StudentProfile studentProfile);
}
