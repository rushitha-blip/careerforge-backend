package com.careerforge.service;

import com.careerforge.model.*;
import com.careerforge.repository.CertificationRepository;
import com.careerforge.repository.ProjectRepository;
import com.careerforge.repository.StudentSkillRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ResumePdfService {

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private ResumeService resumeService;

    public byte[] generateResumePdf(StudentProfile profile) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Color Palette
            Color primaryNavy = new Color(15, 23, 42); // #0f172a
            Color accentBlue = new Color(37, 99, 235); // #2563eb
            Color darkGray = new Color(51, 65, 85);    // #334155
            Color lineGray = new Color(203, 213, 225); // #cbd5e1

            // Fonts
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, primaryNavy);
            Font roleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, accentBlue);
            Font contactFont = FontFactory.getFont(FontFactory.HELVETICA, 9, darkGray);
            Font sectionHeadingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, primaryNavy);
            Font boldTextFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, primaryNavy);
            Font regularTextFont = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, darkGray);
            Font italicFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, darkGray);

            User user = profile.getUser();
            ResumeData resumeData = resumeService.getOrCreateResumeData(profile);

            // 1. HEADER (Candidate Info)
            Paragraph namePara = new Paragraph(user.getName(), nameFont);
            namePara.setAlignment(Element.ALIGN_CENTER);
            namePara.setSpacingAfter(2);
            document.add(namePara);

            String careerGoal = profile.getCareerGoal() != null ? profile.getCareerGoal() : "Full Stack Software Engineer";
            Paragraph rolePara = new Paragraph(careerGoal.toUpperCase(), roleFont);
            rolePara.setAlignment(Element.ALIGN_CENTER);
            rolePara.setSpacingAfter(4);
            document.add(rolePara);

            StringBuilder contactLine = new StringBuilder();
            contactLine.append(user.getEmail());
            if (user.getPhone() != null && !user.getPhone().isBlank()) {
                contactLine.append(" | ").append(user.getPhone());
            }
            if (profile.getLinkedinUrl() != null && !profile.getLinkedinUrl().isBlank()) {
                contactLine.append(" | ").append(profile.getLinkedinUrl());
            }
            if (profile.getGithubUrl() != null && !profile.getGithubUrl().isBlank()) {
                contactLine.append(" | ").append(profile.getGithubUrl());
            }

            Paragraph contactPara = new Paragraph(contactLine.toString(), contactFont);
            contactPara.setAlignment(Element.ALIGN_CENTER);
            contactPara.setSpacingAfter(10);
            document.add(contactPara);

            LineSeparator topDivider = new LineSeparator(1.5f, 100, accentBlue, Element.ALIGN_CENTER, -2);
            document.add(topDivider);
            document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4)));

            // 2. PROFESSIONAL SUMMARY
            if (resumeData.getSummary() != null && !resumeData.getSummary().isBlank()) {
                addSectionHeading(document, "PROFESSIONAL SUMMARY", sectionHeadingFont, lineGray);
                Paragraph summaryPara = new Paragraph(resumeData.getSummary(), regularTextFont);
                summaryPara.setSpacingAfter(10);
                summaryPara.setLeading(14);
                document.add(summaryPara);
            }

            // 3. EDUCATION
            addSectionHeading(document, "EDUCATION", sectionHeadingFont, lineGray);
            PdfPTable eduTable = new PdfPTable(2);
            eduTable.setWidthPercentage(100);
            eduTable.setWidths(new float[]{75, 25});
            eduTable.setSpacingAfter(8);

            String degree = "Bachelor of Technology in " + (profile.getBranch() != null ? profile.getBranch() : "Engineering");
            PdfPCell c1 = new PdfPCell(new Phrase(degree, boldTextFont));
            c1.setBorder(Rectangle.NO_BORDER);
            eduTable.addCell(c1);

            String yearText = profile.getYear() != null ? profile.getYear() : "Final Year";
            PdfPCell c2 = new PdfPCell(new Phrase(yearText, italicFont));
            c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c2.setBorder(Rectangle.NO_BORDER);
            eduTable.addCell(c2);

            String cgpaText = "Cumulative Grade Point Average (CGPA): " + (profile.getCgpa() != null ? profile.getCgpa() : "8.0") + " / 10.0";
            PdfPCell c3 = new PdfPCell(new Phrase(cgpaText, regularTextFont));
            c3.setBorder(Rectangle.NO_BORDER);
            eduTable.addCell(c3);

            PdfPCell c4 = new PdfPCell(new Phrase("First Class with Distinction", italicFont));
            c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c4.setBorder(Rectangle.NO_BORDER);
            eduTable.addCell(c4);

            document.add(eduTable);

            // 4. TECHNICAL SKILLS
            List<StudentSkill> skills = studentSkillRepository.findByStudentProfile(profile);
            if (!skills.isEmpty() || (resumeData.getSkillsCsv() != null && !resumeData.getSkillsCsv().isBlank())) {
                addSectionHeading(document, "TECHNICAL SKILLS & COMPETENCIES", sectionHeadingFont, lineGray);

                StringBuilder skillsString = new StringBuilder();
                if (resumeData.getSkillsCsv() != null && !resumeData.getSkillsCsv().isBlank()) {
                    skillsString.append(resumeData.getSkillsCsv());
                } else {
                    for (int i = 0; i < skills.size(); i++) {
                        StudentSkill ss = skills.get(i);
                        skillsString.append(ss.getSkill().getName())
                                    .append(" (").append(ss.getProficiency()).append(")");
                        if (i < skills.size() - 1) skillsString.append(", ");
                    }
                }

                Paragraph skillsPara = new Paragraph(skillsString.toString(), regularTextFont);
                skillsPara.setSpacingAfter(10);
                skillsPara.setLeading(14);
                document.add(skillsPara);
            }

            // 5. PROJECTS
            List<Project> projects = projectRepository.findByStudentProfile(profile);
            if (!projects.isEmpty()) {
                addSectionHeading(document, "KEY TECHNICAL PROJECTS", sectionHeadingFont, lineGray);

                for (Project p : projects) {
                    Paragraph pTitle = new Paragraph();
                    pTitle.add(new Chunk(p.getTitle(), boldTextFont));
                    if (p.getTechStack() != null && !p.getTechStack().isBlank()) {
                        pTitle.add(new Chunk(" | Tech: " + p.getTechStack(), italicFont));
                    }
                    pTitle.setSpacingAfter(2);
                    document.add(pTitle);

                    if (p.getDescription() != null && !p.getDescription().isBlank()) {
                        Paragraph pDesc = new Paragraph("• " + p.getDescription(), regularTextFont);
                        pDesc.setLeading(13);
                        pDesc.setSpacingAfter(6);
                        document.add(pDesc);
                    }
                }
            }

            // 6. CERTIFICATIONS
            List<Certification> certs = certificationRepository.findByStudentProfile(profile);
            if (!certs.isEmpty()) {
                addSectionHeading(document, "CERTIFICATIONS & CREDENTIALS", sectionHeadingFont, lineGray);
                for (Certification c : certs) {
                    Paragraph certPara = new Paragraph();
                    certPara.add(new Chunk("• " + c.getTitle(), boldTextFont));
                    certPara.add(new Chunk(" — " + c.getIssuingOrganization() + " (" + c.getIssueDate() + ")", regularTextFont));
                    certPara.setSpacingAfter(4);
                    document.add(certPara);
                }
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating Resume PDF: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }

    private void addSectionHeading(Document document, String title, Font font, Color lineColor) throws DocumentException {
        Paragraph heading = new Paragraph(title, font);
        heading.setSpacingBefore(8);
        heading.setSpacingAfter(2);
        document.add(heading);

        LineSeparator sep = new LineSeparator(0.8f, 100, lineColor, Element.ALIGN_CENTER, -1);
        document.add(sep);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 3)));
    }
}
