package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppealEvidence {
    private String id;
    private String name;
    private String type;
    private Long size;
    private String dataUrl;
}