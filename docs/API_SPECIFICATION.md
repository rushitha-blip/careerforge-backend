# CareerForge AI — REST API & Endpoint Specification

## 1. Overview
All asynchronous client requests communicate with Spring Boot controllers using standardized HTTP methods, JSON payloads, and HTTP status codes.

---

## 2. Authentication & Session Endpoints (`AuthController`)

| Endpoint | Method | Role | Request Parameters | Description |
| :--- | :--- | :--- | :--- | :--- |
| `/login` | `GET` | Public | None | Displays login page or redirects if active session exists |
| `/login` | `POST` | Public | `email`, `password` | Authenticates against BCrypt hashes and establishes session |
| `/demo-login/{role}` | `GET` | Public | Path: `student`, `recruiter`, `admin` | Instantly logs in with demo credentials for faculty review |
| `/register/student` | `POST` | Public | `name`, `email`, `password`, `branch`, `year`, `cgpa` | Creates new student account, profile, roadmap, and hashes password |
| `/register/recruiter`| `POST` | Public | `name`, `email`, `password`, `companyName`, `location` | Creates recruiter account and company profile |
| `/logout` | `GET` | Authenticated | None | Invalidates session and clears Spring Security context |

---

## 3. Student Workflow Endpoints (`StudentController` & `ApiController`)

### 3.1 MVC Actions
| Endpoint | Method | Role | Parameters | Description |
| :--- | :--- | :--- | :--- | :--- |
| `/student/dashboard` | `GET` | STUDENT, ADMIN | None | Renders personalized student dashboard with all tabs |
| `/student/profile/update` | `POST` | STUDENT | `branch`, `year`, `cgpa`, `careerGoal`, `bio` | Updates academic information and triggers score recalculation |
| `/student/skill/add` | `POST` | STUDENT | `skillId`, `proficiency` | Maps verified skill to profile and updates readiness score |
| `/student/project/add` | `POST` | STUDENT | `title`, `description`, `techStack`, `githubUrl` | Adds technical portfolio project |
| `/student/cert/add` | `POST` | STUDENT | `title`, `issuingOrganization`, `issueDate` | Adds credential to portfolio |
| `/student/job/apply/{jobId}` | `POST` | STUDENT | Path: `jobId` | Applies to campus drive; prevents duplicate applications |
| `/student/roadmap/toggle/{taskId}` | `POST` | STUDENT | Path: `taskId` | Toggles roadmap milestone completion state |
| `/student/resume/download-pdf` | `GET` | STUDENT | None | Generates and streams dynamic high-fidelity PDF resume |

### 3.2 RESTful JSON Endpoints (`ApiController`)
| Endpoint | Method | Role | Payload / Params | Response |
| :--- | :--- | :--- | :--- | :--- |
| `/api/student/skillgap` | `GET` | STUDENT | `?role=Java Developer` | `SkillGapResultDto` (JSON): owned skills, missing skills, match % |
| `/api/student/readiness`| `GET` | STUDENT | None | `ReadinessScoreDto` (JSON): 5-pillar scores, status, feedback |
| `/api/student/interview/questions` | `GET` | Public | `?category=TECHNICAL` | Array of `MockQuestion` objects |
| `/api/student/interview/submit` | `POST` | STUDENT | `{"category": "TECHNICAL", "answers": {"1": "B"}}` | `MockInterview` result: scores, breakdown, weak areas |
| `/api/student/resume/evaluate` | `POST` | STUDENT | `{"summary": "...", "skillsCsv": "..."}` | `ResumeData` with evaluated ATS compatibility score |
| `/api/student/roadmap/toggle/{taskId}`| `POST` | STUDENT | Path: `taskId` | `{"taskId": 1, "completed": true, "progress": 65}` |

---

## 4. Recruiter Workflow Endpoints (`RecruiterController`)

| Endpoint | Method | Role | Parameters | Description |
| :--- | :--- | :--- | :--- | :--- |
| `/recruiter/dashboard` | `GET` | RECRUITER, ADMIN | `?skillFilter=&branchFilter=&minCgpa=&minReadiness=` | Renders candidate talent discovery pool with sorting & filtering |
| `/recruiter/job/create` | `POST` | RECRUITER | `title`, `company`, `roleCategory`, `packageOffered`, `requiredSkills` | Publishes new campus hiring drive |
| `/recruiter/application/status` | `POST` | RECRUITER | `appId`, `status` | Updates applicant status (APPLIED, SHORTLISTED, etc.) |
| `/recruiter/candidate/{id}/resume-pdf` | `GET` | RECRUITER, ADMIN | Path: `id` (studentProfileId) | Streams candidate's generated PDF resume for inspection |

---

## 5. Administrative Endpoints (`AdminController` & `ApiController`)

| Endpoint | Method | Role | Parameters | Description |
| :--- | :--- | :--- | :--- | :--- |
| `/admin/dashboard` | `GET` | ADMIN | None | Institutional placement analytics, user table, skills, questions |
| `/admin/skill/add` | `POST` | ADMIN | `name`, `category`, `demandLevel` | Extends global skill taxonomy dictionary |
| `/admin/question/add` | `POST` | ADMIN | Category, topic, difficulty, question text, options, answer, explanation | Adds new question to assessment pool |
| `/api/admin/metrics` | `GET` | ADMIN | None | Aggregated student count, job counts, and placement averages |
