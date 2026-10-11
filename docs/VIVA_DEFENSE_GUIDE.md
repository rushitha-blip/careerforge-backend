# CareerForge AI — Faculty Viva Voce Defense & Technical Guide

This document is prepared to assist the student in achieving maximum marks (50/50) during technical project reviews, external viva voce, and faculty demonstrations.

---

## 1. Project Synopsis in 60 Seconds
> "CareerForge AI is a full-stack Java platform engineered to transition engineering students from unstructured placement preparation to data-driven, measurable industry readiness. Built with Spring Boot 3, Spring Data JPA, Hibernate, and MySQL, the platform implements a transparent 5-pillar mathematical model calculating Placement Readiness Scores across Technical Skills, Coding & Aptitude practice, Portfolio Projects, ATS Resume Quality, and Mock Interview performance. It bridges the gap between campus talent and recruiters through automated skill-gap analysis, interactive roadmaps, instant OpenPDF resume exports, and recruiter candidate ranking engines."

---

## 2. Mathematical Scoring Formulas & Algorithms

### 2.1 Placement Readiness Model (100% Weightage)
The score is calculated deterministically by `PlacementReadinessService`:
$$\text{ReadinessScore} = \text{Tech} + \text{CodingAptitude} + \text{Projects} + \text{Resume} + \text{Interview}$$

Where:
1. **Technical Skills ($40\%$ Max):**
   $$\text{TechScore} = \min\left(40, \sum_{i=1}^{n} w(\text{Proficiency}_i)\right)$$
   - $\text{ADVANCED} = 8\text{ points}$
   - $\text{INTERMEDIATE} = 5\text{ points}$
   - $\text{BEGINNER} = 3\text{ points}$
2. **Coding & Aptitude Practice ($20\%$ Max):**
   $$\text{AptitudeScore} = \min\left(20, \text{round}\left(\frac{\text{AvgAptitudeScore}}{100} \times 20\right)\right)$$
3. **Portfolio Projects ($15\%$ Max):**
   $$\text{ProjectScore} = \begin{cases} 
   15 & \text{if Count} \ge 3 \\
   11 & \text{if Count} = 2 \\
   6 & \text{if Count} = 1 \\
   0 & \text{if Count} = 0
   \end{cases}$$
4. **Resume Quality & ATS Compatibility ($15\%$ Max):**
   $$\text{ResumeScore} = \min\left(15, \text{round}\left(\frac{\text{ATS\_Score}}{100} \times 15\right)\right)$$
5. **Communication & Mock Interview Practice ($10\%$ Max):**
   $$\text{InterviewScore} = \min\left(10, \text{round}\left(\frac{\text{AvgHRScore}}{100} \times 10\right)\right)$$

### 2.2 Skill Gap Analysis Algorithm
Implemented in `SkillGapService`:
$$\text{Match Percentage} = \left(\frac{|S_{\text{student}} \cap S_{\text{role}}|}{|S_{\text{role}}|}\right) \times 100$$
$$\text{Missing Skills Set} = S_{\text{role}} \setminus S_{\text{student}}$$
Missing skills directly drive the prioritized learning tasks in the student's roadmap.

---

## 3. Top 15 Anticipated Faculty Viva Questions & Answers

### Q1: Why did you choose Spring Boot 3 over traditional Java EE / Spring MVC?
**Answer:**
Spring Boot 3 eliminates thousands of lines of boilerplate XML configuration through intelligent auto-configuration, includes an embedded Tomcat container for instant deployment, integrates modern Jakarta EE 10 packages, and provides first-class support for Java 21 LTS features like virtual threads and record patterns.

### Q2: How does your database ensure data integrity when a student applies for a job?
**Answer:**
Data integrity is enforced at both the application and database layers:
1. **Database Layer:** The `applications` table contains a composite unique constraint `UNIQUE KEY uk_student_job_app (student_profile_id, job_id)`, preventing duplicate database rows at the storage engine level.
2. **Service Layer:** `JobService.applyForJob` first queries `applicationRepository.findByStudentProfileAndJob(profile, job)`. If an application exists, it returns the existing record idempotently without firing an `INSERT` statement.

### Q3: How do you handle password security in this application?
**Answer:**
We do not store plain-text passwords. All credentials are encrypted using Spring Security's `BCryptPasswordEncoder` utilizing an adaptive work factor (10 rounds of cryptographic salting). BCrypt is resistant to rainbow table and brute-force attacks because each hash contains a randomized salt embedded directly into the 60-character hash string.

### Q4: How is the PDF resume generated on the backend?
**Answer:**
We integrated OpenPDF (`com.github.librepdf:openpdf`), a high-performance LGPL/MPL Java PDF library. In `ResumePdfService`, a `Document` object is assembled with A4 dimensions and precise margins. We construct stylized tables (`PdfPTable`), headers, dividers, and bullet points using student portfolio records directly from the database and stream the binary `byte[]` with `Content-Disposition: attachment; filename="Resume.pdf"`.

### Q5: What is the purpose of Spring Data JPA repositories?
**Answer:**
Spring Data JPA eliminates repetitive JDBC boilerplate. By extending `JpaRepository<T, ID>`, Spring dynamically synthesizes implementation proxies at application startup for standard CRUD operations, pagination, sorting, and derived query methods like `findByStudentProfileOrderByTakenAtDesc`.

### Q6: How does the application support both zero-setup H2 and production MySQL?
**Answer:**
In `application.properties`, we configured the H2 in-memory engine with `MODE=MySQL` compatibility. Because Hibernate generates standard ANSI SQL and JPA entities are decoupled from the physical database, switching to production MySQL requires only updating the `spring.datasource.url` to `jdbc:mysql://localhost:3306/careerforge_db` and changing the driver. The business logic remains 100% identical.

### Q7: Explain how the timed assessment engine works.
**Answer:**
When a student initiates an assessment, `ApiController.getInterviewQuestions` returns questions based on category (`TECHNICAL`, `APTITUDE`, `HR`). Client-side JavaScript (`interview.js`) manages a 10-minute countdown timer with automated submission upon expiry. The answers are sent to `/api/student/interview/submit`, where `MockInterviewService` validates each answer against the correct key, computes category-wise and overall scores, identifies weak topics, persists the attempt in `mock_interviews`, and triggers readiness score recalculation.

### Q8: What design patterns are used in this project?
**Answer:**
- **MVC Pattern (Model-View-Controller):** Strict separation between data models, presentation views, and request controllers.
- **Dependency Injection / Inversion of Control (IoC):** Spring container injects dependencies via `@Autowired`.
- **Repository Pattern:** Encapsulates data access logic behind domain interfaces.
- **DTO Pattern (Data Transfer Object):** Decouples internal database entities from external API contracts (e.g., `JobMatchDto`, `ReadinessScoreDto`).
- **Factory Pattern:** Employed in PDF font and element creation (`FontFactory.getFont`).

### Q9: How is role-based authorization implemented?
**Answer:**
In `SecurityConfig.java`, we define a `SecurityFilterChain`. We designate route patterns with `.hasAnyRole("STUDENT", "ADMIN")`, `.hasAnyRole("RECRUITER", "ADMIN")`, and `.hasRole("ADMIN")`. Spring Security intercepts incoming HTTP requests, inspects the `Authentication` object from `SecurityContextHolder`, and rejects unauthorized attempts with an HTTP 403 Forbidden status.

### Q10: How do you prevent XSS and SQL Injection attacks?
**Answer:**
- **SQL Injection:** All data interactions use Spring Data JPA and Hibernate with parameterized queries and JPQL, ensuring user inputs are treated as literal parameters rather than executable SQL statements.
- **XSS (Cross-Site Scripting):** Thymeleaf server-side templates automatically HTML-escape all dynamic expressions (`th:text`), converting characters like `<`, `>`, and `&` to harmless HTML entities.

### Q11: How does Recruiter Candidate Discovery work?
**Answer:**
In `RecruiterController.dashboard`, candidates are filtered using Java 8 Stream operations based on parameters like academic branch, minimum CGPA, skill tag, and minimum readiness score. The filtered list is sorted descending by readiness score, enabling recruiters to identify top-tier talent instantly.

### Q12: How are unit tests structured in this project?
**Answer:**
We wrote unit tests using JUnit 5 and Mockito in `src/test/java`. We isolated service layer logic (e.g., `PlacementReadinessServiceTest`, `SkillGapServiceTest`, `JobServiceTest`, `AuthServiceTest`) by mocking JPA repository dependencies. This ensures deterministic, millisecond-level execution without requiring external database dependencies.

### Q13: What happens when a student updates a skill proficiency level?
**Answer:**
When a student modifies a skill (e.g., advancing Java from Intermediate to Advanced), `StudentController.addSkill` updates the `StudentSkill` record. Immediately following persistence, it calls `placementReadinessService.calculateAndUpdateReadinessScore(profile)`, recalculating the technical score (+8 instead of +5) and saving the updated readiness score to `StudentProfile`.

### Q14: How does the ATS resume evaluation calculate compatibility?
**Answer:**
In `ResumeService.updateAndEvaluateResume`, the student's summary and skills CSV are evaluated against high-demand keywords (REST API, Git, SQL, Docker, Microservices), length criteria, and academic CGPA metrics. A compatibility score (0-100) is generated along with contextual optimization advice.

### Q15: How can this application be deployed in an enterprise cloud environment?
**Answer:**
1. Package the application as an executable standalone JAR via `mvn clean package`.
2. Containerize the JAR using a multi-stage Dockerfile based on `eclipse-temurin:21-jre-alpine`.
3. Deploy the container on AWS Elastic Container Service (ECS) or Kubernetes (EKS).
4. Connect to an AWS RDS MySQL multi-AZ database instance with automated daily backups.
