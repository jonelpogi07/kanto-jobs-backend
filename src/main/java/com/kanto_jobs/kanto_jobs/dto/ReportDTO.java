package com.kanto_jobs.kanto_jobs.dto;

import com.kanto_jobs.kanto_jobs.domain.ReportStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportDTO {
    private Long id;
    private Long reporterId;
    private Long targetUserId;
    private Long jobId;
    private Long applicationId;
    private String reason;
    private String note;
    private ReportStatus status;
    private LocalDateTime reportedAt;
    private LocalDateTime appealDeadline;
    private String appealReason;
    private LocalDateTime appealedAt;
    private Boolean refunded;
    private LocalDateTime refundedAt;
}