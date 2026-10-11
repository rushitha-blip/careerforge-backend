package com.careerforge.service;

import com.careerforge.model.RoadmapTask;
import com.careerforge.model.StudentProfile;
import com.careerforge.repository.RoadmapTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoadmapService {

    @Autowired
    private RoadmapTaskRepository roadmapTaskRepository;

    @Transactional
    public List<RoadmapTask> getOrCreateRoadmapForStudent(StudentProfile profile) {
        List<RoadmapTask> tasks = roadmapTaskRepository.findByStudentProfileOrderByStepNumberAsc(profile);
        if (!tasks.isEmpty()) {
            return tasks;
        }

        // Initialize tailored roadmap tasks based on target role
        String role = profile.getCareerGoal() != null ? profile.getCareerGoal() : "Full Stack Developer";
        List<RoadmapTask> defaultTasks = new ArrayList<>();

        defaultTasks.add(new RoadmapTask(
                profile, 1,
                "Java Fundamentals & OOP Mastery",
                "Deep dive into Classes, Encapsulation, Inheritance, Polymorphism, Abstract classes, Interfaces, and Exception Handling with Java 21 features.",
                "Core Fundamentals",
                "https://dev.java/learn/"
        ));

        defaultTasks.add(new RoadmapTask(
                profile, 2,
                "Data Structures, Collections & Algorithms",
                "Master ArrayList, LinkedList, HashMap, HashSet, PriorityQueue, Binary Search Trees, and Graph Traversal with problem-solving on LeetCode.",
                "Algorithms",
                "https://leetcode.com/problemset/all/"
        ));

        defaultTasks.add(new RoadmapTask(
                profile, 3,
                "Relational Database Design & MySQL Optimization",
                "Design normalized schemas (1NF, 2NF, 3NF), write complex JOIN queries, subqueries, indexing strategies, transactions (ACID), and query profiling.",
                "Databases",
                "https://dev.mysql.com/doc/"
        ));

        defaultTasks.add(new RoadmapTask(
                profile, 4,
                "Spring Boot 3, Spring Data JPA & Hibernate",
                "Build scalable enterprise backends with Spring Boot, Spring MVC controllers, repositories, validation, exception handling, and ORM entity mappings.",
                "Backend Frameworks",
                "https://spring.io/guides"
        ));

        defaultTasks.add(new RoadmapTask(
                profile, 5,
                "RESTful API Design & Spring Security",
                "Implement secure role-based access control (RBAC), password hashing with BCrypt, session security, CORS, and standardized REST error contracts.",
                "Security & APIs",
                "https://docs.spring.io/spring-security/reference/"
        ));

        defaultTasks.add(new RoadmapTask(
                profile, 6,
                "Modern Frontend Integration (HTML5, CSS3, JS & Bootstrap 5)",
                "Develop responsive, accessible user interfaces with clean grid layouts, Chart.js visualizations, DOM event handling, and Fetch API communication.",
                "Frontend",
                "https://getbootstrap.com/docs/5.3/"
        ));

        defaultTasks.add(new RoadmapTask(
                profile, 7,
                "System Architecture, Docker & Production Deployment",
                "Containerize applications with Docker, configure production profiles, manage environment variables, and deploy to cloud environments.",
                "DevOps & Deployment",
                "https://docs.docker.com/"
        ));

        // Mark the first step completed as default starter baseline
        defaultTasks.get(0).setCompleted(true);

        return roadmapTaskRepository.saveAll(defaultTasks);
    }

    @Transactional
    public RoadmapTask toggleTaskCompletion(Long taskId, StudentProfile profile) {
        RoadmapTask task = roadmapTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Roadmap task not found"));

        if (!task.getStudentProfile().getId().equals(profile.getId())) {
            throw new SecurityException("Unauthorized access to student roadmap task");
        }

        task.setCompleted(!task.isCompleted());
        return roadmapTaskRepository.save(task);
    }

    public int getRoadmapProgressPercentage(StudentProfile profile) {
        long total = roadmapTaskRepository.countByStudentProfile(profile);
        if (total == 0) return 0;
        long completed = roadmapTaskRepository.countByStudentProfileAndCompletedTrue(profile);
        return (int) Math.round(((double) completed / total) * 100);
    }
}
