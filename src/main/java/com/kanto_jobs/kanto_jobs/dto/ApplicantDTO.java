package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ApplicantDTO extends UserSummaryDTO {
    private java.util.List<String> skills;
}