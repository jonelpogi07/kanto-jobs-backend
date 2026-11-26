package com.kanto_jobs.kanto_jobs.dto;

import com.kanto_jobs.kanto_jobs.domain.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApplicationDTO {
    private Long id;
    private Long jobId;
    private JobPostDTO job;
    private ApplicantDTO applicant;
    private String pitch;
    private ApplicationStatus status;
    private ContactInfoDTO applicantContact;
    private ContactInfoDTO posterContact;
    private LocalDateTime createdAt;
}