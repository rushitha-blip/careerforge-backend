# CareerForge AI — Faculty Live Demonstration Walkthrough Script

Follow this step-by-step guide to deliver a flawless, high-impact demonstration during faculty review and practical lab evaluations.

---

## Pre-Demo Setup Checklist
1. **Start the Application:**
   Open PowerShell in `C:\CareerForgeAI` and execute:
   ```powershell
   & "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" spring-boot:run
   ```
   Or run the generated JAR:
   ```powershell
   java -jar target/careerforge-ai-1.0.0.jar
   ```
2. **Access the Application:**
   Open Google Chrome or Microsoft Edge and navigate to:
   `http://localhost:8085`

---

## Step 1: Landing Page Overview (2 Minutes)
1. Point out the refined visual design: dark navy shell, crisp card typography, and clean contrast.
2. Show the **Live Platform Statistics Bar**:
   - Enrolled Candidates (from database)
   - Verified Recruiters (from database)
   - Open Campus Drives (from database)
   - Average Placement Readiness Score (calculated dynamically)
3. Highlight that every metric is backed by actual relational records.
4. Open the **"Quick Demo Accounts"** button in the header navbar to show 1-click access for Student, Recruiter, and Admin personas.

---

## Step 2: Student Experience Walkthrough (5 Minutes)
1. **Log In as Student:**
   - Click **"Demo Student (Rahul Sharma)"** or sign in with:
     - Email: `student@careerforge.ai`
     - Password: `student123`
2. **Overview & Readiness Pillar Tab:**
   - Demonstrate the **Readiness Score Gauge** (e.g., 83 / 100).
   - Walk the faculty through the transparent 5-pillar breakdown:
     - Technical Skills: 35 / 40
     - Coding & Aptitude: 16 / 20
     - Projects: 11 / 15
     - Resume Quality: 13 / 15
     - Mock Interview: 8 / 10
   - Show the dynamic recommendations list based on missing items.
3. **Skill Gap Analyzer Tab:**
   - Switch the target role dropdown (e.g. from *Full Stack Developer* to *Java Developer* or *Data Scientist*).
   - Show how the match percentage recalculates instantly via asynchronous fetch (`/api/student/skillgap`).
   - Point out missing skills and personalized action items.
4. **Learning Roadmap Tab:**
   - Show the step-by-step milestones (Java Fundamentals -> Databases -> Spring Boot -> Security -> Frontend -> Cloud).
   - Click **"Mark Done"** on Step 2.
   - Show how the milestone completion progress bar advances and persists across refreshes.
5. **Interactive Resume Builder & PDF Export Tab:**
   - Show the live ATS Compatibility Score and suggestions.
   - Click **"Download PDF Resume"**.
   - Show the generated A4 PDF resume opening directly in the browser with professional typography, contact info, skills, projects, and credentials.
6. **Mock Interview & Assessment Engine Tab:**
   - Click **"Technical Quiz (Java/Spring)"**.
   - Show the 10-minute timer ticking down.
   - Answer the multiple-choice questions and click **"Submit Assessment"**.
   - Show the SweetAlert score modal, breakdown, explanations, and how the attempt is logged in the history table.
7. **Job Discovery Tab:**
   - Show recommended jobs sorted by student skill match percentage.
   - Click **"Apply with Profile"** on an open job opening.
   - Show the success alert and how duplicate submissions are prevented.

---

## Step 3: Recruiter Command Center Walkthrough (3 Minutes)
1. **Switch to Recruiter:**
   - Navigate to `/demo-login/recruiter` or log in with:
     - Email: `recruiter@careerforge.ai`
     - Password: `recruiter123`
2. **Talent Discovery Engine:**
   - Show all registered student candidates ranked by readiness score.
   - Demonstrate the filters:
     - Filter by Academic Branch (e.g., *Computer Science & Engg*)
     - Filter by Min CGPA (e.g., *8.0*)
     - Filter by Min Readiness Score (e.g., *75*)
   - Click **"View Resume"** on Rahul Sharma to stream his live PDF resume directly from the server.
3. **Candidate Applications Tab:**
   - View submitted student applications.
   - Update Rahul Sharma's status from `APPLIED` to `SHORTLISTED` using the dropdown.
   - Show how the status persists in the database.
4. **Post New Job Modal:**
   - Open the modal to show the recruiter publishing a new hiring opening with salary package and required skills.

---

## Step 4: University Placement Admin Walkthrough (2 Minutes)
1. **Switch to Admin:**
   - Navigate to `/demo-login/admin` or log in with:
     - Email: `admin@careerforge.ai`
     - Password: `admin123`
2. **Institutional Analytics Tab:**
   - Review cohort statistics and the placement readiness trend chart rendered via Chart.js.
   - Point out the category distribution (Placement Ready vs. High Potential vs. Needs Focus).
3. **Question Bank Tab:**
   - Review the institutional question bank across Technical, Aptitude, and HR categories.
   - Open **"+ Add Question"** to demonstrate adding a question with options, correct answer, and explanation.
4. **Export Placement Report:**
   - Click **"Print Placement Report"** to show a print-friendly institutional report.

---

## Summary of Key Strengths to Emphasize to Evaluators
- **Real Full-Stack Implementation:** Genuine Java Spring Boot backend, Spring Security BCrypt hashing, Spring Data JPA, and Hibernate transactions.
- **Data Persistence:** Nothing is hardcoded or fake in browser memory; everything is saved to the relational database.
- **High-Fidelity PDF Generation:** Programmatic A4 resume creation using OpenPDF.
- **Mathematical Transparency:** Deterministic, explainable 5-pillar scoring algorithm.
- **Automated Testing:** 100% passing JUnit 5 and Mockito unit test suites.
