package com.careerforge.repository;

import com.careerforge.model.RoadmapTask;
import com.careerforge.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapTaskRepository extends JpaRepository<RoadmapTask, Long> {
    List<RoadmapTask> findByStudentProfileOrderByStepNumberAsc(StudentProfile studentProfile);
    long countByStudentProfile(StudentProfile studentProfile);
    long countByStudentProfileAndCompletedTrue(StudentProfile studentProfile);
}
