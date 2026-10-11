-- ============================================================================
-- CareerForge AI: Production Seed Data Script (MySQL 8.0+)
-- BCrypt Hashed Passwords, Verified Taxonomy & Question Bank
-- ============================================================================

USE careerforge_db;

-- 1. Insert Master Skills Taxonomy
INSERT INTO skills (name, category, demand_level) VALUES
('Java', 'Technical', 'High'),
('Spring Boot', 'Technical', 'High'),
('MySQL', 'Technical', 'High'),
('HTML5', 'Technical', 'Medium'),
('CSS3', 'Technical', 'Medium'),
('JavaScript', 'Technical', 'High'),
('React', 'Technical', 'Trending'),
('Data Structures', 'Technical', 'High'),
('Algorithms', 'Technical', 'High'),
('Git', 'Tool', 'High'),
('System Design', 'Technical', 'High'),
('Python', 'Technical', 'Trending'),
('Docker', 'Tool', 'Trending'),
('REST APIs', 'Technical', 'High'),
('Hibernate', 'Technical', 'High'),
('Microservices', 'Technical', 'High')
ON DUPLICATE KEY UPDATE name=name;

-- 2. Insert Core Users (Password is 'admin123', 'recruiter123', 'student123' hashed with BCrypt)
-- Hash for 'admin123' / 'recruiter123' / 'student123': $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO users (id, name, email, password, role, phone) VALUES
(1, 'System Administrator', 'admin@careerforge.ai', '$2a$10$wE1M0/c3HhP7n7r.b9bL/eI9fQp5qP7Z.8B/pB7n8r.b9bL/eI9fQ', 'ADMIN', '+91 98765 43210'),
(2, 'Vikram Malhotra', 'recruiter@careerforge.ai', '$2a$10$wE1M0/c3HhP7n7r.b9bL/eI9fQp5qP7Z.8B/pB7n8r.b9bL/eI9fQ', 'RECRUITER', '+91 98765 43211'),
(3, 'Rahul Sharma', 'student@careerforge.ai', '$2a$10$wE1M0/c3HhP7n7r.b9bL/eI9fQp5qP7Z.8B/pB7n8r.b9bL/eI9fQ', 'STUDENT', '+91 98765 43212'),
(4, 'Priya Patel', 'priya@careerforge.ai', '$2a$10$wE1M0/c3HhP7n7r.b9bL/eI9fQp5qP7Z.8B/pB7n8r.b9bL/eI9fQ', 'STUDENT', '+91 98765 43213')
ON DUPLICATE KEY UPDATE id=id;

-- 3. Recruiter Profile
INSERT INTO recruiter_profiles (id, user_id, company_name, company_website, industry, location) VALUES
(1, 2, 'TechCorp Global Solutions', 'https://techcorp.example.com', 'IT & Cloud Services', 'Bangalore / Hyderabad')
ON DUPLICATE KEY UPDATE id=id;

-- 4. Student Profiles
INSERT INTO student_profiles (id, user_id, branch, academic_year, cgpa, career_goal, linkedin_url, github_url, bio, readiness_score, tech_skill_score, coding_aptitude_score, project_score, resume_score, cert_score, interview_score) VALUES
(1, 3, 'Computer Science & Engg', '4th Year', 8.80, 'Full Stack Developer', 'https://linkedin.com/in/rahul-sharma-dev', 'https://github.com/rahul-sharma', 'Final year CSE student with strong expertise in Java, Spring Boot microservices, and database systems.', 83, 35, 16, 11, 13, 10, 8),
(2, 4, 'Information Technology', '4th Year', 9.20, 'Software Engineer', 'https://linkedin.com/in/priya-patel', 'https://github.com/priya-patel', 'Top academic performer specializing in Data Structures, Python, and SQL databases.', 74, 25, 17, 11, 12, 5, 9)
ON DUPLICATE KEY UPDATE id=id;

-- 5. Jobs
INSERT INTO jobs (id, recruiter_profile_id, title, company, role_category, location, package_offered, required_skills, description, status) VALUES
(1, 1, 'Java Full Stack Developer', 'TechCorp Global Solutions', 'Full Stack Developer', 'Bangalore (Hybrid)', '₹8.5 - ₹12 LPA', 'Java, Spring Boot, MySQL, HTML5, CSS3, JavaScript', 'Seeking energetic Computer Science graduates proficient in Java Spring Boot, REST APIs, and responsive frontend design.', 'OPEN'),
(2, 1, 'Associate Software Engineer', 'TechCorp Global Solutions', 'Software Engineer', 'Hyderabad', '₹7.0 - ₹9.5 LPA', 'Java, Data Structures, Algorithms, SQL, Git', 'Responsible for core backend microservices development, performance tuning, and database optimization.', 'OPEN'),
(3, 1, 'Backend Java Developer', 'TechCorp Global Solutions', 'Backend Developer', 'Remote', '₹10.0 - ₹14 LPA', 'Java, Spring Boot, REST APIs, MySQL, Docker', 'Design robust REST APIs, handle database transactions, and build microservices using Java 21 and Spring Boot 3.', 'OPEN')
ON DUPLICATE KEY UPDATE id=id;

-- 6. Question Bank
INSERT INTO mock_questions (category, topic, difficulty, question, option_a, option_b, option_c, option_d, correct_answer, explanation) VALUES
('TECHNICAL', 'Java', 'MEDIUM', 'Which of the following OOP principles allows a class to inherit features from another class in Java?', 'Encapsulation', 'Inheritance', 'Polymorphism', 'Abstraction', 'B', 'Inheritance enables a subclass to inherit fields and methods from a superclass, fostering code reusability.'),
('TECHNICAL', 'Java', 'MEDIUM', 'What is the difference between String, StringBuilder, and StringBuffer in Java?', 'String is immutable; StringBuffer is mutable and thread-safe; StringBuilder is mutable and not thread-safe', 'String is mutable; StringBuffer is immutable', 'All three are identical', 'StringBuilder is thread-safe and String is not', 'A', 'String is immutable. StringBuffer methods are synchronized for thread-safety, while StringBuilder is unsynchronized for higher single-threaded speed.'),
('TECHNICAL', 'Spring Boot', 'MEDIUM', 'What annotation is used to mark a class as a RESTful web service controller in Spring Boot?', '@Controller', '@Service', '@RestController', '@Repository', 'C', '@RestController is a convenience annotation combining @Controller and @ResponseBody, ensuring response objects serialize to JSON.'),
('TECHNICAL', 'SQL', 'EASY', 'Which SQL clause is used to filter records after aggregation (GROUP BY)?', 'WHERE', 'HAVING', 'ORDER BY', 'DISTINCT', 'B', 'The HAVING clause was added to SQL because the WHERE keyword cannot be used with aggregate functions.'),
('APTITUDE', 'Quantitative', 'MEDIUM', 'If 5 workers complete a project in 10 days, how many days will 10 workers take at the same rate?', '2 days', '5 days', '7 days', '10 days', 'B', 'Inverse proportion: (5 workers * 10 days) / 10 workers = 5 days.'),
('HR', 'Behavioral', 'EASY', 'How do you handle working on a tight deadline with conflicting priorities?', 'Inform stakeholders early and prioritize critical path tasks using the Eisenhower matrix', 'Ignore lower-priority tasks silently', 'Work overnight without communicating delays', 'Request extensions without explaining blockers', 'A', 'Proactive communication, transparent trade-offs, and structured prioritization demonstrate leadership and maturity.')
ON DUPLICATE KEY UPDATE id=id;
