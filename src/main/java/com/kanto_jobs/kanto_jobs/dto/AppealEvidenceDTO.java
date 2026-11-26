package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppealEvidenceDTO {
    private String id;
    private String name;
    private String type;
    private Long size;
    private String dataUrl;
}