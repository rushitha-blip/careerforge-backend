# ⚙️ CareerForge AI — Java Spring Boot Enterprise Backend

> **Major Capstone Project — Full Stack Development Using Java (Backend Subsystem)**  
> Engineered with **Java 21 LTS**, **Spring Boot 3.2.3**, **Spring Security 6 (BCrypt)**, **Spring Data JPA**, **Hibernate 6.4**, **OpenPDF**, **H2 / MySQL 8.0**, and **JUnit 5 / Mockito**.

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.2-blue.svg?logo=springsecurity)](https://spring.io/projects/spring-security)
[![Tests](https://img.shields.io/badge/JUnit%205-12%20Passed%20%7C%200%20Failed-success.svg)](https://github.com/)
[![Docker](https://img.shields.io/badge/Docker-Multi--Stage%20Build-blue.svg?logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 📌 Subsystem Overview

The **CareerForge AI Backend** powers an intelligent placement readiness and talent discovery platform. It exposes secure RESTful APIs, executes multi-variable algorithmic scoring, enforces cryptographic role-based access control, generates server-side ATS-compliant PDF resumes, and provides transaction-safe data persistence.

### Key Backend Responsibilities
1. **Mathematical Scoring Engine:** Implements the 5-Pillar Placement Readiness algorithm ($0 - 100$).
2. **Skill Gap Diagnostics:** Vector-compares student skill matrices against industry benchmarks for 6 high-demand target roles.
3. **Dynamic Roadmap Engine:** Generates and updates multi-phase career roadmap tasks persisted in the database.
4. **Document Generation Engine:** Uses OpenPDF to programmatically compile dynamic A4 PDF resumes with verified credentials and GitHub projects.
5. **Role-Based Access Control (RBAC):** Spring Security 6 with 10-round `BCryptPasswordEncoder` protecting `/student/**`, `/recruiter/**`, and `/admin/**` routes.
6. **Talent Discovery & Ranking Engine:** Filters and ranks candidates for campus recruiters based on computed readiness percentiles.

---

## 🏗️ Layered Architecture & Request Lifecycle

```
[ HTTP / REST Request ]
         │
         ▼
[ Spring Security Filter Chain ] ──> BCrypt Password Encoder / SecurityContext
         │
         ▼
[ Controller Layer ] ──────────────> AuthController, StudentController, RecruiterController,
         │                           AdminController, ApiController
         ▼
[ DTO & Validation Layer ] ────────> ReadinessScoreDto, SkillGapResultDto, ApplicationDto
         │
         ▼
[ Service Layer ] ─────────────────> PlacementReadinessService, SkillGapService, RoadmapService,
         │                           ResumePdfService, JobService, CustomUserDetailsService
         ▼
[ Repository Layer ] ──────────────> 14 Spring Data JPA Repositories (CRUD & Derived Queries)
         │
         ▼
[ Database Layer ] ────────────────> Dual-Mode: H2 In-Memory (Dev) & MySQL 8.0 (Prod)
```

---

## 🧮 5-Pillar Placement Readiness Model

Implemented in `PlacementReadinessService.java`:

$$\text{Readiness Score} = (S_{\text{tech}} \times 0.40) + (S_{\text{apt}} \times 0.20) + (S_{\text{proj}} \times 0.15) + (S_{\text{res}} \times 0.15) + (S_{\text{int}} \times 0.10)$$

* **Technical Skills ($S_{\text{tech}}$ — 40%):** Weighted based on cataloged competency proficiencies and domain breadth.
* **Coding & Aptitude ($S_{\text{apt}}$ — 20%):** Algorithmic problem accuracy and logical aptitude evaluations.
* **Projects ($S_{\text{proj}}$ — 15%):** Count, complexity, and technology diversity of verified GitHub repositories.
* **Resume Quality ($S_{\text{res}}$ — 15%):** ATS keyword compatibility, action-verb density, and profile completeness.
* **Mock Interviews ($S_{\text{int}}$ — 10%):** Timed technical and behavioural screening assessment performance.

---

## 🔌 REST API Endpoints Summary

### Authentication & Profile APIs
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/login` | Authenticate user via BCrypt verification | Public |
| `GET` | `/demo-login/{role}` | Instant 1-click authentication (`student`, `recruiter`, `admin`) | Public |
| `POST` | `/register/student` | Register candidate & auto-seed baseline roadmap | Public |
| `POST` | `/register/recruiter`| Register corporate recruiter profile | Public |
| `GET` | `/logout` | Invalidate HTTP session & clear SecurityContext | Authenticated |

### Student Subsystem APIs
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/student/dashboard` | Retrieve full student placement dossier & metrics | `ROLE_STUDENT` |
| `POST` | `/student/profile/update` | Update academic branch, year, CGPA, target role | `ROLE_STUDENT` |
| `POST` | `/student/roadmap/toggle/{taskId}` | Toggle milestone completion & sync database | `ROLE_STUDENT` |
| `GET` | `/student/resume/download-pdf` | Stream generated A4 PDF resume (OpenPDF) | `ROLE_STUDENT` |
| `POST` | `/student/interview/submit` | Evaluate timed mock interview & persist score | `ROLE_STUDENT` |
| `POST` | `/student/job/apply/{jobId}` | Submit job application to recruiter pipeline | `ROLE_STUDENT` |

### Recruiter Subsystem APIs
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/recruiter/dashboard` | View candidate pool ranked by readiness score | `ROLE_RECRUITER` |
| `GET` | `/recruiter/candidate/{id}/resume-pdf` | Stream applicant PDF resume in-browser | `ROLE_RECRUITER` |
| `POST` | `/recruiter/application/{id}/status` | Update candidate pipeline (`SHORTLISTED`, etc.) | `ROLE_RECRUITER` |
| `POST` | `/recruiter/job/create` | Publish campus recruitment opening | `ROLE_RECRUITER` |

### Admin Subsystem APIs
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/admin/dashboard` | University-wide placement statistics & trend graphs | `ROLE_ADMIN` |
| `POST` | `/admin/question/add` | Insert question into curated technical/aptitude bank | `ROLE_ADMIN` |

---

## 🧪 Automated Testing Suite (JUnit 5 & Mockito)

The backend includes 12 automated unit tests validating algorithms, security boundaries, and service logic:

```bash
# Run all unit tests
mvn test
```

### Verified Test Results:
* `PlacementReadinessServiceTest`: 3 tests (Pillar weighting, null profile handling, zero-score edge cases) — **Passed**
* `SkillGapServiceTest`: 2 tests (Vector benchmark comparison, missing skill extraction) — **Passed**
* `JobServiceTest`: 3 tests (Applicant ranking, application pipeline updates, matching index) — **Passed**
* `AuthServiceTest`: 4 tests (BCrypt password encoding, duplicate email prevention, role assignment) — **Passed**

**Total: 12 Passed, 0 Failures, 0 Errors, 0 Skipped.**

---

## 🗄️ Relational Database Schema (3NF)

14 normalized relational entities mapped using Spring Data JPA and Hibernate:
- `users`: Core authentication table with BCrypt hashed credentials and roles.
- `student_profiles`: Academic data, target career goal, CGPA, placement readiness score.
- `roadmap_tasks`: Persistent learning milestone tasks and completion statuses.
- `skills` & `student_skills`: Industry skill taxonomy and verified proficiency levels.
- `projects` & `certifications`: Technical portfolios linked to student profiles.
- `jobs` & `job_applications`: Recruitment requisitions and application pipeline states.
- `mock_interviews` & `questions`: Automated technical assessment records.

DDL scripts for standalone MySQL 8.0 deployment are located in `database/schema-mysql.sql` and `database/data-mysql.sql`.

---

## 🚀 Cloud Deployment Instructions

### Option 1: Deploy with Docker on Render (Recommended)
1. Push this repository to GitHub: `https://github.com/<your-username>/careerforge-backend`.
2. Log in to [Render.com](https://render.com) and click **New +** $\rightarrow$ **Web Service**.
3. Select your GitHub repository. Render will automatically detect the included `Dockerfile` and `render.yaml`.
4. Click **Deploy Web Service**.
5. Your backend will be live at: `https://careerforge-backend.onrender.com`.

### Option 2: Run Locally
```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/careerforge-ai-1.0.0.jar
```

---

## 📄 License
This backend subsystem is developed for academic demonstration and capstone evaluation under the MIT License.
