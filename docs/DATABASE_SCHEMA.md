# CareerForge AI — Relational Database Schema Specification

## 1. Relational Design Philosophy
The database schema for **CareerForge AI** is normalized to **Third Normal Form (3NF)** to ensure zero transitive dependencies and prevent data anomalies. All tables utilize the `InnoDB` engine with explicit foreign keys, indexes on critical filter columns, and transactional boundaries managed by Spring Data JPA.

---

## 2. Table Specifications

### 2.1 `users`
Core identity table powering authentication and role authorization.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Unique User Identifier |
| `name` | `VARCHAR(100)` | NOT NULL | User's Full Display Name |
| `email` | `VARCHAR(150)` | NOT NULL, UNIQUE, INDEX | Login Identifier |
| `password` | `VARCHAR(255)` | NOT NULL | BCrypt One-Way Hashed Password |
| `role` | `VARCHAR(30)` | NOT NULL, INDEX | `STUDENT`, `RECRUITER`, `ADMIN` |
| `phone` | `VARCHAR(30)` | NULLABLE | Primary Contact Number |
| `created_at` | `TIMESTAMP` | DEFAULT CURRENT_TIMESTAMP | Registration Timestamp |

### 2.2 `student_profiles`
Academic and placement assessment tracking for registered students.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Profile Identifier |
| `user_id` | `BIGINT` | NOT NULL, UNIQUE, FK -> `users(id)` | Parent User Account |
| `branch` | `VARCHAR(100)` | NOT NULL | Academic Engineering Department |
| `academic_year` | `VARCHAR(30)` | NOT NULL | `3rd Year`, `4th Year` |
| `cgpa` | `DOUBLE` | NOT NULL, DEFAULT 8.00 | Cumulative GPA (Scale 10.0) |
| `career_goal` | `VARCHAR(100)` | DEFAULT 'Full Stack Developer' | Target Industry Job Track |
| `linkedin_url` | `VARCHAR(255)` | NULLABLE | LinkedIn Profile URL |
| `github_url` | `VARCHAR(255)` | NULLABLE | GitHub Portfolio Repository |
| `bio` | `TEXT` | NULLABLE | Professional Profile Summary |
| `readiness_score`| `INT` | DEFAULT 0, INDEX | Overall Placement Readiness (0-100) |
| `tech_skill_score`| `INT`| DEFAULT 0 | Technical Pillar Score (Max 40) |
| `coding_aptitude_score`| `INT`| DEFAULT 0 | Coding/Aptitude Practice (Max 20) |
| `project_score` | `INT` | DEFAULT 0 | Portfolio Projects (Max 15) |
| `resume_score` | `INT` | DEFAULT 0 | Resume Completeness/ATS (Max 15) |
| `interview_score`| `INT`| DEFAULT 0 | Mock Interview Performance (Max 10)|

### 2.3 `recruiter_profiles`
Corporate hiring profile for registered recruiting partners.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Recruiter Identifier |
| `user_id` | `BIGINT` | NOT NULL, UNIQUE, FK -> `users(id)` | Parent User Account |
| `company_name` | `VARCHAR(150)` | NOT NULL | Registered Enterprise Name |
| `company_website`| `VARCHAR(255)`| NULLABLE | Corporate Web Portal |
| `industry` | `VARCHAR(100)` | NULLABLE | Industry Sector (e.g. IT, FinTech) |
| `location` | `VARCHAR(100)` | NULLABLE | Hiring Base / Office Location |

### 2.4 `skills`
Master global skill taxonomy dictionary.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Skill Identifier |
| `name` | `VARCHAR(100)` | NOT NULL, UNIQUE, INDEX | Skill Name (e.g. Java, Spring Boot) |
| `category` | `VARCHAR(50)` | NOT NULL | `Technical`, `Tool`, `Framework` |
| `demand_level` | `VARCHAR(30)` | NOT NULL, DEFAULT 'High' | `High`, `Trending`, `Medium` |

### 2.5 `student_skills`
Associative junction table mapping verified skills to student profiles.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Junction ID |
| `student_profile_id`| `BIGINT` | NOT NULL, FK -> `student_profiles(id)` | Student Profile Reference |
| `skill_id` | `BIGINT` | NOT NULL, FK -> `skills(id)` | Skill Reference |
| `proficiency` | `VARCHAR(30)` | NOT NULL, DEFAULT 'INTERMEDIATE' | `BEGINNER`, `INTERMEDIATE`, `ADVANCED` |
| *Composite Unique* | | `UNIQUE(student_profile_id, skill_id)` | Prevents duplicate skill mappings |

### 2.6 `projects`
Technical portfolio projects demonstrating practical full-stack competencies.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Project Identifier |
| `student_profile_id`| `BIGINT`| NOT NULL, FK -> `student_profiles(id)` | Project Owner |
| `title` | `VARCHAR(150)` | NOT NULL | Project Name |
| `description` | `TEXT` | NOT NULL | Architectural Description & Metrics |
| `tech_stack` | `VARCHAR(255)` | NOT NULL | Comma-delimited technologies used |
| `github_url` | `VARCHAR(255)` | NULLABLE | Source Code Repository Link |
| `live_demo_url`| `VARCHAR(255)`| NULLABLE | Deployed Application URL |

### 2.7 `certifications`
Verified credentials and third-party certifications.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Credential Identifier |
| `student_profile_id`| `BIGINT`| NOT NULL, FK -> `student_profiles(id)` | Student Profile Reference |
| `title` | `VARCHAR(150)` | NOT NULL | Certification Title |
| `issuing_organization`| `VARCHAR(150)`| NOT NULL | Issuer (e.g. Oracle, AWS) |
| `issue_date` | `VARCHAR(50)` | NOT NULL | Issue Month & Year |
| `credential_url`| `VARCHAR(255)`| NULLABLE | Verification Link |

### 2.8 `jobs`
Campus placement drives and openings published by recruiters.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Job Identifier |
| `recruiter_profile_id`| `BIGINT`| NOT NULL, FK -> `recruiter_profiles(id)` | Posting Recruiter |
| `title` | `VARCHAR(150)` | NOT NULL | Job Opening Title |
| `company` | `VARCHAR(150)` | NOT NULL | Hiring Entity |
| `role_category`| `VARCHAR(100)`| NOT NULL | Target Role Cluster |
| `location` | `VARCHAR(100)` | NOT NULL | Job Location |
| `package_offered`| `VARCHAR(100)`| NOT NULL | Annual Compensation Package (LPA) |
| `required_skills`| `TEXT` | NOT NULL | Comma-delimited Required Skills |
| `description` | `TEXT` | NOT NULL | Role Description & Eligibility Criteria |
| `status` | `VARCHAR(30)` | NOT NULL, DEFAULT 'OPEN' | `OPEN`, `CLOSED`, `ARCHIVED` |
| `posted_date` | `TIMESTAMP` | DEFAULT CURRENT_TIMESTAMP | Posting Timestamp |

### 2.9 `applications`
Student job applications and recruiter hiring pipelines.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Application Identifier |
| `student_profile_id`| `BIGINT`| NOT NULL, FK -> `student_profiles(id)` | Applicant Student |
| `job_id` | `BIGINT` | NOT NULL, FK -> `jobs(id)` | Target Job Opening |
| `match_score` | `INT` | NOT NULL, DEFAULT 0 | Algorithmic Match % at application |
| `status` | `VARCHAR(30)` | NOT NULL, DEFAULT 'APPLIED' | `APPLIED`, `SHORTLISTED`, `INTERVIEWING`, `ACCEPTED`, `REJECTED` |
| `application_date`| `TIMESTAMP`| DEFAULT CURRENT_TIMESTAMP | Submission Timestamp |
| *Composite Unique* | | `UNIQUE(student_profile_id, job_id)` | Prevents duplicate applications |

### 2.10 `roadmap_tasks`
Persistent step-by-step personalized learning curriculum.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Task Identifier |
| `student_profile_id`| `BIGINT`| NOT NULL, FK -> `student_profiles(id)` | Student Roadmap Owner |
| `step_number` | `INT` | NOT NULL | Sequence Order (1, 2, 3...) |
| `title` | `VARCHAR(150)` | NOT NULL | Learning Objective Title |
| `description` | `TEXT` | NOT NULL | Curriculum Description |
| `category` | `VARCHAR(50)` | NOT NULL | Domain Track |
| `resource_link`| `VARCHAR(255)`| NULLABLE | Documentation / Course URL |
| `completed` | `BOOLEAN` | DEFAULT FALSE | Completion State |
| `completed_at` | `TIMESTAMP` | NULLABLE | Completion Timestamp |

### 2.11 `mock_questions`
Question bank pool for technical and aptitude assessments.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Question Identifier |
| `category` | `VARCHAR(30)` | NOT NULL, INDEX | `TECHNICAL`, `APTITUDE`, `HR` |
| `topic` | `VARCHAR(100)` | NOT NULL | Domain Topic (Java, SQL, Logic) |
| `difficulty` | `VARCHAR(30)` | NOT NULL, DEFAULT 'MEDIUM' | `EASY`, `MEDIUM`, `HARD` |
| `question` | `TEXT` | NOT NULL | Question Formulation |
| `option_a` | `VARCHAR(255)` | NOT NULL | Choice Option A |
| `option_b` | `VARCHAR(255)` | NOT NULL | Choice Option B |
| `option_c` | `VARCHAR(255)` | NOT NULL | Choice Option C |
| `option_d` | `VARCHAR(255)` | NOT NULL | Choice Option D |
| `correct_answer`| `VARCHAR(5)`| NOT NULL | Key (`A`, `B`, `C`, `D`) |
| `explanation` | `TEXT` | NULLABLE | Solution Rationale |

### 2.12 `mock_interviews`
Historical records of student quiz attempts and performance.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Attempt Identifier |
| `student_profile_id`| `BIGINT`| NOT NULL, FK -> `student_profiles(id)` | Test Candidate |
| `category` | `VARCHAR(30)` | NOT NULL | Quiz Category |
| `overall_score`| `INT` | NOT NULL | Overall Percentage Score |
| `technical_score`| `INT` | NOT NULL | Technical Topic Score |
| `hr_score` | `INT` | NOT NULL | Behavioral / HR Score |
| `aptitude_score`| `INT` | NOT NULL | Numerical / Logical Score |
| `feedback` | `TEXT` | NULLABLE | Performance Feedback |
| `weak_areas` | `TEXT` | NULLABLE | Detected Improvement Areas |
| `taken_at` | `TIMESTAMP` | DEFAULT CURRENT_TIMESTAMP | Test Timestamp |

### 2.13 `resume_data`
ATS keyword analysis and resume customization.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Resume Record Identifier |
| `student_profile_id`| `BIGINT`| NOT NULL, UNIQUE, FK -> `student_profiles(id)` | Candidate Profile |
| `summary` | `TEXT` | NULLABLE | Professional Profile Summary |
| `ats_score` | `INT` | DEFAULT 0 | Simulated ATS Score (0-100) |
| `template_name`| `VARCHAR(50)`| DEFAULT 'Modern Standard' | Visual Template Style |
| `skills_csv` | `TEXT` | NULLABLE | Flattened Keyword String |
| `ats_suggestions`| `TEXT` | NULLABLE | Improvement Recommendations |
