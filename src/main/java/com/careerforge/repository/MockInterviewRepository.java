package com.careerforge.repository;

import com.careerforge.model.MockInterview;
import com.careerforge.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {
    List<MockInterview> findByStudentProfileOrderByTakenAtDesc(StudentProfile studentProfile);
}
