package com.careerforge.repository;

import com.careerforge.model.ResumeData;
import com.careerforge.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeDataRepository extends JpaRepository<ResumeData, Long> {
    Optional<ResumeData> findByStudentProfile(StudentProfile studentProfile);
}
