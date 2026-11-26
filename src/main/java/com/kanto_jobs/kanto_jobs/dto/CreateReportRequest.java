package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateReportRequest {
    private Long jobId;
    private Long applicationId;
    private String reason;
    private String note;
}