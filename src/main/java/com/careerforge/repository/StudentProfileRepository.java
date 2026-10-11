package com.careerforge.repository;

import com.careerforge.model.StudentProfile;
import com.careerforge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUser(User user);
    Optional<StudentProfile> findByUserId(Long userId);
    List<StudentProfile> findByBranch(String branch);
    List<StudentProfile> findByCgpaGreaterThanEqual(Double minCgpa);
    
    @Query("SELECT AVG(s.readinessScore) FROM StudentProfile s")
    Double getAverageReadinessScore();
}
