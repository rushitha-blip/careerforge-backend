# CareerForge AI — System Architecture & Technical Specification

## 1. Executive Problem Statement
Campus placements represent a pivotal milestone for engineering students. However, traditional placement processes suffer from significant structural gaps:
- **Asymmetric Career Guidance:** Students lack quantitative, transparent benchmarks comparing their current skill proficiency against target industry roles (e.g., Full Stack Java Engineer, Backend Architect).
- **Unstructured Placement Preparation:** Students often practice disparate technical topics without a coherent, sequenced roadmap tailored to their verified weaknesses.
- **Low ATS Compatibility:** Student resumes frequently fail applicant tracking system (ATS) filters due to lack of standard industry keyword density and clear metrics.
- **Recruiter Candidate Inefficiency:** Campus recruiters sift through hundreds of unstructured resumes without objective, verified placement-readiness scores.

**CareerForge AI** solves these challenges by providing an integrated, data-driven career development and talent matching platform built on an enterprise 4-tier Java Spring Boot and relational database architecture.

---

## 2. 4-Tier Enterprise Layered Architecture

```
+--------------------------------------------------------------------------+
|                       1. PRESENTATION LAYER (CLIENT)                     |
|  - HTML5, CSS3 Glassmorphism, Bootstrap 5.3                              |
|  - Chart.js 4.0 Interactive Radar & Trend Visualizations                 |
|  - Fetch API Asynchronous REST Consumer & SweetAlert2 Interactive Alerts |
|  - Thymeleaf Server-Side Dynamic Template Rendering                      |
+--------------------------------------------------------------------------+
                                     |
                                     | HTTP / HTTPS Requests
                                     v
+--------------------------------------------------------------------------+
|                       2. CONTROLLER / API LAYER                          |
|  - Spring MVC Web Controllers:                                           |
|      * LandingController   (Unified Portal & Landing Shell)             |
|      * AuthController      (Session & BCrypt Login/Registration)        |
|      * StudentController   (Portfolio, Roadmap, Resume PDF Download)    |
|      * RecruiterController (Candidate Discovery & Application Status)   |
|      * AdminController     (Institutional Analytics & Question Bank)    |
|  - RESTful API Controller:                                               |
|      * ApiController (/api/student/**, /api/admin/**)                   |
|  - Spring Security Filter Chain (RBAC: ROLE_STUDENT, ROLE_RECRUITER)     |
+--------------------------------------------------------------------------+
                                     |
                                     | Service Calls & DTO Mapping
                                     v
+--------------------------------------------------------------------------+
|                       3. SERVICE / BUSINESS LOGIC LAYER                  |
|  - PlacementReadinessService (5-Pillar Mathematical Scoring Engine)      |
|  - SkillGapService           (Role Benchmark Set Difference Analysis)   |
|  - RoadmapService            (Persistent Dynamic Step Milestone Engine)  |
|  - ResumeService             (ATS Density Algorithm & Profile Parsing)   |
|  - ResumePdfService          (OpenPDF High-Fidelity PDF Generation)     |
|  - MockInterviewService      (Timed Assessment Grading & Weak Area Eval) |
|  - JobService                (Candidate-Job Match Percentage Algorithm)  |
|  - AdminAnalyticsService     (Institutional Placement Cohort Analytics)  |
|  - AuthService               (BCrypt Hashing & Spring Security Context)  |
+--------------------------------------------------------------------------+
                                     |
                                     | JPA Entities / Transactions (@Transactional)
                                     v
+--------------------------------------------------------------------------+
|                  4. DATA ACCESS & PERSISTENCE LAYER                      |
|  - Spring Data JPA Repositories (13 Interfaces extending JpaRepository)  |
|  - Hibernate ORM Dialect (Automatic DDL & Transaction Boundaries)        |
|  - In-Memory H2 Database (Default Zero-Config Mode)                      |
|  - Production MySQL 8.0 Engine (InnoDB, Foreign Key Integrity, Indexes)  |
+--------------------------------------------------------------------------+
```

---

## 3. Security Architecture & Role-Based Access Control (RBAC)

Spring Security is configured in `SecurityConfig.java`:
- **Password Protection:** Standard `BCryptPasswordEncoder` with 10 salt rounds hashes all student and recruiter passwords upon registration.
- **Session Security:** Server-side `HttpSession` coupled with `SecurityContextHolder.getContext().setAuthentication(...)` ensures that every request is securely verified.
- **Route Authorization:**
  - `/student/**` & `/api/student/**`: Restrict to `ROLE_STUDENT` and `ROLE_ADMIN`.
  - `/recruiter/**` & `/api/recruiter/**`: Restrict to `ROLE_RECRUITER` and `ROLE_ADMIN`.
  - `/admin/**` & `/api/admin/**`: Strictly reserved for `ROLE_ADMIN`.
  - Public routes: `/`, `/login`, `/register/**`, `/demo-login/**`, `/css/**`, `/js/**`, `/h2-console/**`.

---

## 4. Entity-Relationship Diagram (ERD) Overview

```
                      +-------------------+
                      |       User        |
                      +-------------------+
                      | PK id             |
                      |    email (UNIQUE) |
                      |    password       |
                      |    role           |
                      +-------------------+
                         /             \
                   1 : 1/               \1 : 1
                       v                 v
        +-------------------+       +--------------------+
        |  StudentProfile   |       |  RecruiterProfile  |
        +-------------------+       +--------------------+
        | PK id             |       | PK id              |
        | FK user_id        |       | FK user_id         |
        |    branch, cgpa   |       |    company_name    |
        |    readinessScore |       +--------------------+
        +-------------------+                 |
          |   |    |    |                     | 1 : N
          |   |    |    +-------+             v
    1 : N |   |    | 1 : N      | 1:1   +---------------+
          |   |    v            |       |     Job       |
          |   | +-------------+ |       +---------------+
          |   | | Project     | |       | PK id         |
          |   | +-------------+ |       | FK recruiter  |
          |   v                 v       |    title, pkg |
          | +---------------+ +-------+ +---------------+
          | | Certification | |Resume |         |
          | +---------------+ +-------+         | 1 : N
          v                                     v
     +--------------+                    +---------------+
     | StudentSkill |                    |  Application  |
     +--------------+                    +---------------+
     | FK profile   |                    | FK profile_id |
     | FK skill_id  |                    | FK job_id     |
     +--------------+                    |    matchScore |
            |                            |    status     |
            v                            +---------------+
     +--------------+
     |    Skill     |
     +--------------+
     | PK id        |
     |    name      |
     |    category  |
     +--------------+
```
