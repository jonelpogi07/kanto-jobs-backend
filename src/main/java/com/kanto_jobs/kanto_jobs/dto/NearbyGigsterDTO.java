package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NearbyGigsterDTO {
    private Long id;
    private String fullName;
    private String avatarUrl;
    private String barangay;
    private String city;
    private Double ratingAverage;
    private Integer ratingCount;
    private Integer completedGigs;
    private Boolean phoneVerified;
    private java.util.List<String> skills;
    private Boolean isNegotiable;
}