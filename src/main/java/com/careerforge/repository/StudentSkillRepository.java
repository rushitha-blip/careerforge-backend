package com.careerforge.repository;

import com.careerforge.model.Skill;
import com.careerforge.model.StudentProfile;
import com.careerforge.model.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {
    List<StudentSkill> findByStudentProfile(StudentProfile studentProfile);
    Optional<StudentSkill> findByStudentProfileAndSkill(StudentProfile studentProfile, Skill skill);
    void deleteByStudentProfileAndSkill(StudentProfile studentProfile, Skill skill);
}
