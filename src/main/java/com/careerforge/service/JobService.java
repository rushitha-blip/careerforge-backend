package com.careerforge.service;

import com.careerforge.dto.JobMatchDto;
import com.careerforge.model.*;
import com.careerforge.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    public List<JobMatchDto> getRecommendedJobsForStudent(StudentProfile profile) {
        List<Job> allJobs = jobRepository.findByStatus("OPEN");
        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentProfile(profile);
        Set<String> ownedSkills = studentSkills.stream()
                .map(ss -> ss.getSkill().getName().toLowerCase())
                .collect(Collectors.toSet());

        List<Application> studentApps = applicationRepository.findByStudentProfile(profile);
        Set<Long> appliedJobIds = studentApps.stream()
                .map(a -> a.getJob().getId())
                .collect(Collectors.toSet());

        List<JobMatchDto> matches = new ArrayList<>();

        for (Job job : allJobs) {
            String[] reqArray = job.getRequiredSkills() != null ? job.getRequiredSkills().split(",") : new String[0];
            List<String> matching = new ArrayList<>();
            List<String> missing = new ArrayList<>();

            for (String req : reqArray) {
                String cleanReq = req.trim();
                if (ownedSkills.contains(cleanReq.toLowerCase())) {
                    matching.add(cleanReq);
                } else {
                    missing.add(cleanReq);
                }
            }

            int reqTotal = reqArray.length > 0 ? reqArray.length : 1;
            int matchPercent = (int) Math.round(((double) matching.size() / reqTotal) * 100);

            // Boost score slightly if CGPA is above 8.0
            if (profile.getCgpa() != null && profile.getCgpa() >= 8.0) {
                matchPercent = Math.min(100, matchPercent + 5);
            }

            boolean isApplied = appliedJobIds.contains(job.getId());
            matches.add(new JobMatchDto(job, matchPercent, matching, missing, isApplied));
        }

        matches.sort((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()));
        return matches;
    }

    public Application applyForJob(StudentProfile profile, Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job not found"));

        Optional<Application> existing = applicationRepository.findByStudentProfileAndJob(profile, job);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Compute match percentage
        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentProfile(profile);
        Set<String> owned = studentSkills.stream().map(s -> s.getSkill().getName().toLowerCase()).collect(Collectors.toSet());
        String[] reqs = job.getRequiredSkills() != null ? job.getRequiredSkills().split(",") : new String[0];
        int matched = 0;
        for (String r : reqs) {
            if (owned.contains(r.trim().toLowerCase())) matched++;
        }
        int matchScore = reqs.length > 0 ? (int) Math.round(((double) matched / reqs.length) * 100) : 75;

        Application app = new Application(profile, job, matchScore);
        return applicationRepository.save(app);
    }
}
