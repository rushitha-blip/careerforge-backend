package com.careerforge.service;

import com.careerforge.model.*;
import com.careerforge.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SeedDataService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MockQuestionRepository mockQuestionRepository;

    @Autowired
    private MockInterviewRepository mockInterviewRepository;

    @Autowired
    private ResumeDataRepository resumeDataRepository;

    @Autowired
    private PlacementReadinessService placementReadinessService;

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        System.out.println(">>> [CareerForge AI] Initializing Comprehensive Seed Data with BCrypt & Roadmap...");

        // 1. Seed Skills Catalog
        List<Skill> skills = Arrays.asList(
                new Skill("Java", "Technical", "High"),
                new Skill("Spring Boot", "Technical", "High"),
                new Skill("MySQL", "Technical", "High"),
                new Skill("HTML5", "Technical", "Medium"),
                new Skill("CSS3", "Technical", "Medium"),
                new Skill("JavaScript", "Technical", "High"),
                new Skill("React", "Technical", "Trending"),
                new Skill("Data Structures", "Technical", "High"),
                new Skill("Algorithms", "Technical", "High"),
                new Skill("Git", "Tool", "High"),
                new Skill("System Design", "Technical", "High"),
                new Skill("Python", "Technical", "Trending"),
                new Skill("Docker", "Tool", "Trending"),
                new Skill("REST APIs", "Technical", "High"),
                new Skill("Hibernate", "Technical", "High"),
                new Skill("Microservices", "Technical", "High")
        );
        skillRepository.saveAll(skills);

        // 2. Seed Admin User
        User admin = new User("System Administrator", "admin@careerforge.ai", passwordEncoder.encode("admin123"), "ADMIN", "+91 98765 43210");
        userRepository.save(admin);

        // 3. Seed Recruiter User & Profile
        User recruiterUser = new User("Vikram Malhotra", "recruiter@careerforge.ai", passwordEncoder.encode("recruiter123"), "RECRUITER", "+91 98765 43211");
        userRepository.save(recruiterUser);

        RecruiterProfile recruiterProfile = new RecruiterProfile(recruiterUser, "TechCorp Global Solutions", "https://techcorp.example.com", "IT & Cloud Services", "Bangalore / Hyderabad");
        recruiterProfileRepository.save(recruiterProfile);

        // 4. Seed Jobs
        Job job1 = new Job(recruiterProfile, "Java Full Stack Developer", "TechCorp Global Solutions", "Full Stack Developer", "Bangalore (Hybrid)", "₹8.5 - ₹12 LPA", "Java, Spring Boot, MySQL, HTML5, CSS3, JavaScript", "Seeking energetic Computer Science graduates proficient in Java Spring Boot, REST APIs, and responsive frontend design.");
        Job job2 = new Job(recruiterProfile, "Associate Software Engineer", "TechCorp Global Solutions", "Software Engineer", "Hyderabad", "₹7.0 - ₹9.5 LPA", "Java, Data Structures, Algorithms, SQL, Git", "Responsible for core backend microservices development, performance tuning, and database optimization.");
        Job job3 = new Job(recruiterProfile, "Backend Java Developer", "TechCorp Global Solutions", "Backend Developer", "Remote", "₹10.0 - ₹14 LPA", "Java, Spring Boot, REST APIs, MySQL, Docker", "Design robust REST APIs, handle database transactions, and build microservices using Java 21 and Spring Boot 3.");
        jobRepository.saveAll(Arrays.asList(job1, job2, job3));

        // 5. Seed Student User 1 (Rahul Sharma - High Readiness)
        User studentUser1 = new User("Rahul Sharma", "student@careerforge.ai", passwordEncoder.encode("student123"), "STUDENT", "+91 98765 43212");
        userRepository.save(studentUser1);

        StudentProfile profile1 = new StudentProfile(studentUser1, "Computer Science & Engg", "4th Year", 8.8, "Full Stack Developer");
        profile1.setBio("Final year CSE student with strong expertise in Java, Spring Boot microservices, and database systems. Passionate about full-stack engineering and software architecture.");
        profile1.setGithubUrl("https://github.com/rahul-sharma");
        profile1.setLinkedinUrl("https://linkedin.com/in/rahul-sharma-dev");
        studentProfileRepository.save(profile1);

        // Assign Skills to Student 1
        List<StudentSkill> s1Skills = Arrays.asList(
                new StudentSkill(profile1, skillRepository.findByNameIgnoreCase("Java").get(), "ADVANCED"),
                new StudentSkill(profile1, skillRepository.findByNameIgnoreCase("Spring Boot").get(), "INTERMEDIATE"),
                new StudentSkill(profile1, skillRepository.findByNameIgnoreCase("MySQL").get(), "ADVANCED"),
                new StudentSkill(profile1, skillRepository.findByNameIgnoreCase("HTML5").get(), "ADVANCED"),
                new StudentSkill(profile1, skillRepository.findByNameIgnoreCase("CSS3").get(), "INTERMEDIATE"),
                new StudentSkill(profile1, skillRepository.findByNameIgnoreCase("JavaScript").get(), "INTERMEDIATE"),
                new StudentSkill(profile1, skillRepository.findByNameIgnoreCase("REST APIs").get(), "ADVANCED"),
                new StudentSkill(profile1, skillRepository.findByNameIgnoreCase("Git").get(), "ADVANCED")
        );
        studentSkillRepository.saveAll(s1Skills);

        // Add Projects for Student 1
        Project p1 = new Project(profile1, "E-Commerce Microservices Engine", "Scalable multi-tenant e-commerce backend with Spring Boot, JWT auth, and MySQL database.", "Java, Spring Boot, MySQL", "https://github.com/rahul/ecommerce-api", "https://demo-shop.example.com");
        Project p2 = new Project(profile1, "Automated Career Analytics Platform", "Placement readiness calculator and ATS resume scoring web application.", "Java, Spring Boot, Bootstrap 5", "https://github.com/rahul/careerforge", "https://careerforge.example.com");
        projectRepository.saveAll(Arrays.asList(p1, p2));

        // Add Certifications for Student 1
        Certification c1 = new Certification(profile1, "AWS Certified Cloud Practitioner", "Amazon Web Services", "Aug 2025", "https://aws.amazon.com/verification");
        Certification c2 = new Certification(profile1, "Oracle Certified Professional: Java SE 17", "Oracle", "Jan 2026", "https://oracle.com/cert/12345");
        certificationRepository.saveAll(Arrays.asList(c1, c2));

        // Add Mock Interview History for Student 1
        MockInterview mi1 = new MockInterview(profile1, "TECHNICAL", 85, 90, 80, 85, "Excellent grasp on OOPs principles, Java memory management, and Spring MVC lifecycle.", "System Design basics");
        mockInterviewRepository.save(mi1);

        // Add Resume Data for Student 1
        ResumeData rd1 = new ResumeData(profile1, "Passionate Java Full Stack Engineer with strong expertise in Spring Boot, MySQL relational modeling, and modern responsive UI development.", 88, "Modern Standard", "Java, Spring Boot, MySQL, HTML5, CSS3, JavaScript, Git, REST APIs", "", "", "", "Strong keyword density and clear project metrics.");
        resumeDataRepository.save(rd1);

        // Initialize Roadmap for Student 1
        roadmapService.getOrCreateRoadmapForStudent(profile1);

        // Calculate Student 1 Score
        placementReadinessService.calculateAndUpdateReadinessScore(profile1);

        // Apply Student 1 to Job 1
        Application app1 = new Application(profile1, job1, 88);
        applicationRepository.save(app1);

        // 6. Seed Student User 2 (Priya Patel - Medium Readiness)
        User studentUser2 = new User("Priya Patel", "priya@careerforge.ai", passwordEncoder.encode("student123"), "STUDENT", "+91 98765 43213");
        userRepository.save(studentUser2);

        StudentProfile profile2 = new StudentProfile(studentUser2, "Information Technology", "4th Year", 9.2, "Software Engineer");
        profile2.setBio("Top academic performer specializing in Data Structures, Python, and SQL databases.");
        studentProfileRepository.save(profile2);

        List<StudentSkill> s2Skills = Arrays.asList(
                new StudentSkill(profile2, skillRepository.findByNameIgnoreCase("Java").get(), "INTERMEDIATE"),
                new StudentSkill(profile2, skillRepository.findByNameIgnoreCase("Data Structures").get(), "ADVANCED"),
                new StudentSkill(profile2, skillRepository.findByNameIgnoreCase("Python").get(), "ADVANCED"),
                new StudentSkill(profile2, skillRepository.findByNameIgnoreCase("MySQL").get(), "INTERMEDIATE"),
                new StudentSkill(profile2, skillRepository.findByNameIgnoreCase("Algorithms").get(), "ADVANCED")
        );
        studentSkillRepository.saveAll(s2Skills);

        Project p3 = new Project(profile2, "Algorithmic Path Finder", "Visualizer for graph search algorithms including A* and Dijkstra.", "Python, Algorithms", "https://github.com/priya/path-finder", null);
        projectRepository.save(p3);

        MockInterview mi2 = new MockInterview(profile2, "APTITUDE", 80, 75, 80, 85, "Strong quantitative reasoning and logical deduction. Recommended to build more full-stack projects.", "Spring Boot Architecture");
        mockInterviewRepository.save(mi2);

        ResumeData rd2 = new ResumeData(profile2, "Analytical software engineer with solid problem-solving foundation in data structures and relational queries.", 75, "Modern Standard", "Java, Data Structures, Python, MySQL, Algorithms", "", "", "", "Consider adding full-stack web framework projects.");
        resumeDataRepository.save(rd2);

        roadmapService.getOrCreateRoadmapForStudent(profile2);
        placementReadinessService.calculateAndUpdateReadinessScore(profile2);

        // Apply Student 2 to Job 2
        Application app2 = new Application(profile2, job2, 85);
        applicationRepository.save(app2);

        // 7. Seed Student User 3 (Arjun Verma - 3rd Year)
        User studentUser3 = new User("Arjun Verma", "arjun@careerforge.ai", passwordEncoder.encode("student123"), "STUDENT", "+91 98765 43214");
        userRepository.save(studentUser3);

        StudentProfile profile3 = new StudentProfile(studentUser3, "AI & Data Science", "3rd Year", 7.9, "Backend Developer");
        profile3.setBio("Pre-final year student exploring backend microservices and database internals.");
        studentProfileRepository.save(profile3);

        List<StudentSkill> s3Skills = Arrays.asList(
                new StudentSkill(profile3, skillRepository.findByNameIgnoreCase("Java").get(), "BEGINNER"),
                new StudentSkill(profile3, skillRepository.findByNameIgnoreCase("MySQL").get(), "INTERMEDIATE"),
                new StudentSkill(profile3, skillRepository.findByNameIgnoreCase("Git").get(), "INTERMEDIATE")
        );
        studentSkillRepository.saveAll(s3Skills);

        roadmapService.getOrCreateRoadmapForStudent(profile3);
        placementReadinessService.calculateAndUpdateReadinessScore(profile3);

        // 8. Seed Rich Question Bank (Technical, Aptitude, HR)
        List<MockQuestion> questions = Arrays.asList(
                // Technical - Java & Spring Boot
                new MockQuestion("TECHNICAL", "Java", "MEDIUM", "Which of the following OOP principles allows a class to inherit features from another class in Java?", "Encapsulation", "Inheritance", "Polymorphism", "Abstraction", "B", "Inheritance enables a subclass to inherit fields and methods from a superclass, fostering code reusability."),
                new MockQuestion("TECHNICAL", "Java", "MEDIUM", "What is the difference between String, StringBuilder, and StringBuffer in Java?", "String is immutable; StringBuffer is mutable and thread-safe; StringBuilder is mutable and not thread-safe", "String is mutable; StringBuffer is immutable", "All three are identical", "StringBuilder is thread-safe and String is not", "A", "String is immutable. StringBuffer methods are synchronized for thread-safety, while StringBuilder is unsynchronized for higher single-threaded speed."),
                new MockQuestion("TECHNICAL", "Spring Boot", "MEDIUM", "What annotation is used to mark a class as a RESTful web service controller in Spring Boot?", "@Controller", "@Service", "@RestController", "@Repository", "C", "@RestController is a convenience annotation combining @Controller and @ResponseBody, ensuring response objects serialize to JSON."),
                new MockQuestion("TECHNICAL", "Spring Boot", "HARD", "How does Spring Boot resolve dependency injection when multiple beans implement the same interface?", "It throws NoUniqueBeanDefinitionException unless @Primary or @Qualifier is used", "It arbitrarily picks the first bean", "It instantiates both simultaneously", "It converts the bean to a singleton", "A", "Spring container requires disambiguation via @Primary or @Qualifier when more than one candidate bean matches an injection point."),
                new MockQuestion("TECHNICAL", "SQL", "EASY", "Which SQL clause is used to filter records after aggregation (GROUP BY)?", "WHERE", "HAVING", "ORDER BY", "DISTINCT", "B", "The HAVING clause was added to SQL because the WHERE keyword cannot be used with aggregate functions."),
                new MockQuestion("TECHNICAL", "SQL", "MEDIUM", "What does the ACID property 'Isolation' guarantee in relational database transactions?", "Transactions execute concurrently without causing data inconsistencies", "All changes are permanent", "Either all operations succeed or none", "Data conforms to all schema constraints", "A", "Isolation ensures that concurrent execution of transactions leaves the database in the same state as if they were executed sequentially."),
                new MockQuestion("TECHNICAL", "Data Structures", "MEDIUM", "What is the worst-case time complexity of searching in a balanced Binary Search Tree (e.g. AVL / Red-Black)?", "O(1)", "O(n)", "O(log n)", "O(n log n)", "C", "A self-balancing BST maintains height O(log n), ensuring search, insert, and delete take logarithmic time in the worst case."),
                
                // Aptitude & Quantitative Reasoning
                new MockQuestion("APTITUDE", "Quantitative", "MEDIUM", "If 5 workers complete a project in 10 days, how many days will 10 workers take at the same rate?", "2 days", "5 days", "7 days", "10 days", "B", "Inverse proportion: (5 workers * 10 days) / 10 workers = 5 days."),
                new MockQuestion("APTITUDE", "Quantitative", "MEDIUM", "A train 150m long passes a pole in 9 seconds. What is the speed of the train in km/h?", "50 km/h", "60 km/h", "72 km/h", "80 km/h", "B", "Speed = Distance / Time = 150m / 9s = 50/3 m/s. Converting to km/h: (50/3) * (18/5) = 60 km/h."),
                new MockQuestion("APTITUDE", "Logical", "EASY", "Find the next number in the series: 3, 7, 15, 31, 63, ...", "95", "127", "128", "125", "B", "Pattern: Each number is 2x + 1. (63 * 2) + 1 = 127."),
                new MockQuestion("APTITUDE", "Logical", "MEDIUM", "Pointing to a photograph, a woman says: 'His mother is the only daughter of my mother.' How is the woman related to the person in the photograph?", "Sister", "Mother", "Aunt", "Daughter", "B", "The only daughter of the woman's mother is the woman herself. Therefore, she is the person's mother."),
                
                // HR & Behavioral
                new MockQuestion("HR", "Behavioral", "EASY", "How do you handle working on a tight deadline with conflicting priorities?", "Inform stakeholders early and prioritize critical path tasks using the Eisenhower matrix", "Ignore lower-priority tasks silently", "Work overnight without communicating delays", "Request extensions without explaining blockers", "A", "Proactive communication, transparent trade-offs, and structured prioritization demonstrate leadership and maturity."),
                new MockQuestion("HR", "Behavioral", "MEDIUM", "When presenting a technical solution to non-technical stakeholders, what approach is most effective?", "Focus on business value, user outcomes, and simplified analogies rather than implementation jargon", "Present low-level code snippets directly", "Insist that they learn technical terminology first", "Avoid answering architectural questions", "A", "Translating technical architecture into measurable business outcomes is an essential engineering communication skill.")
        );
        mockQuestionRepository.saveAll(questions);

        System.out.println(">>> [CareerForge AI] Comprehensive Seed Data Loaded Successfully!");
    }
}
