package com.kanto_jobs.kanto_jobs.dto;

import com.kanto_jobs.kanto_jobs.domain.AppealStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppealDTO {
    private Long id;
    private Long reportId;
    private Long appellantId;
    private String reason;
    private List<AppealEvidenceDTO> evidence;
    private AppealStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
}