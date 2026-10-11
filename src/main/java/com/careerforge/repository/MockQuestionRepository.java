package com.careerforge.repository;

import com.careerforge.model.MockQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockQuestionRepository extends JpaRepository<MockQuestion, Long> {
    List<MockQuestion> findByCategory(String category);
    List<MockQuestion> findByTopic(String topic);
}
