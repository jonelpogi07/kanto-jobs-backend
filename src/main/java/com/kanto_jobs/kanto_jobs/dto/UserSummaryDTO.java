package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserSummaryDTO {
    private Long id;
    private String fullName;
    private String avatarUrl;
    private String barangay;
    private String city;
    private Boolean phoneVerified;
    private Double ratingAverage;
    private Integer ratingCount;
    private Integer completedGigs;
}