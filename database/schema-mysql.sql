-- ============================================================================
-- CareerForge AI: Production Relational Database Schema (MySQL 8.0+)
-- Intelligent Campus Placement & Career Development Platform
-- ============================================================================

CREATE DATABASE IF NOT EXISTS careerforge_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE careerforge_db;

-- 1. Users Table (Core Authentication & Role Management)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL, -- STUDENT, RECRUITER, ADMIN
    phone VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Student Profiles
CREATE TABLE IF NOT EXISTS student_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    branch VARCHAR(100) NOT NULL,
    academic_year VARCHAR(30) NOT NULL,
    cgpa DOUBLE NOT NULL DEFAULT 8.00,
    career_goal VARCHAR(100) DEFAULT 'Full Stack Developer',
    linkedin_url VARCHAR(255),
    github_url VARCHAR(255),
    bio TEXT,
    readiness_score INT DEFAULT 0,
    tech_skill_score INT DEFAULT 0,
    coding_aptitude_score INT DEFAULT 0,
    project_score INT DEFAULT 0,
    resume_score INT DEFAULT 0,
    cert_score INT DEFAULT 0,
    interview_score INT DEFAULT 0,
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_student_readiness (readiness_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Recruiter Profiles
CREATE TABLE IF NOT EXISTS recruiter_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    company_name VARCHAR(150) NOT NULL,
    company_website VARCHAR(255),
    industry VARCHAR(100),
    location VARCHAR(100),
    CONSTRAINT fk_recruiter_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Global Skills Catalog
CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    demand_level VARCHAR(30) NOT NULL DEFAULT 'High',
    INDEX idx_skill_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Student Verified Skills
CREATE TABLE IF NOT EXISTS student_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    proficiency VARCHAR(30) NOT NULL DEFAULT 'INTERMEDIATE', -- BEGINNER, INTERMEDIATE, ADVANCED
    CONSTRAINT fk_ss_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_ss_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_skill (student_profile_id, skill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Technical Portfolio Projects
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    tech_stack VARCHAR(255) NOT NULL,
    github_url VARCHAR(255),
    live_demo_url VARCHAR(255),
    CONSTRAINT fk_project_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Student Certifications
CREATE TABLE IF NOT EXISTS certifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    issuing_organization VARCHAR(150) NOT NULL,
    issue_date VARCHAR(50) NOT NULL,
    credential_url VARCHAR(255),
    CONSTRAINT fk_cert_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Campus Job Postings
CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruiter_profile_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    company VARCHAR(150) NOT NULL,
    role_category VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    package_offered VARCHAR(100) NOT NULL,
    required_skills TEXT NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    posted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_recruiter FOREIGN KEY (recruiter_profile_id) REFERENCES recruiter_profiles(id) ON DELETE CASCADE,
    INDEX idx_job_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Student Job Applications
CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    match_score INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'APPLIED', -- APPLIED, SHORTLISTED, INTERVIEWING, ACCEPTED, REJECTED
    application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_app_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_job_app (student_profile_id, job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Question Bank (Technical, Aptitude, HR)
CREATE TABLE IF NOT EXISTS mock_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(30) NOT NULL, -- TECHNICAL, APTITUDE, HR
    topic VARCHAR(100) NOT NULL,
    difficulty VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
    question TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_answer VARCHAR(5) NOT NULL,
    explanation TEXT,
    INDEX idx_question_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. Mock Interview / Assessment Attempts
CREATE TABLE IF NOT EXISTS mock_interviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id BIGINT NOT NULL,
    category VARCHAR(30) NOT NULL,
    overall_score INT NOT NULL,
    technical_score INT NOT NULL,
    hr_score INT NOT NULL,
    aptitude_score INT NOT NULL,
    feedback TEXT,
    weak_areas TEXT,
    taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interview_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. ATS Resume Data
CREATE TABLE IF NOT EXISTS resume_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id BIGINT NOT NULL UNIQUE,
    summary TEXT,
    ats_score INT DEFAULT 0,
    template_name VARCHAR(50) DEFAULT 'Modern Standard',
    skills_csv TEXT,
    experience_json TEXT,
    education_json TEXT,
    projects_json TEXT,
    ats_suggestions TEXT,
    CONSTRAINT fk_resume_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. Persistent Learning Roadmap Tasks
CREATE TABLE IF NOT EXISTS roadmap_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id BIGINT NOT NULL,
    step_number INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    resource_link VARCHAR(255),
    completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP NULL,
    CONSTRAINT fk_roadmap_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    INDEX idx_roadmap_student (student_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(30) DEFAULT 'INFO',
    is_read BOOLEAN DEFAULT FALSE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
