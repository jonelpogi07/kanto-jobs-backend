package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateAppealRequest {
    private String reason;
    private List<AppealEvidenceDTO> evidence;
}