package com.kanto_jobs.kanto_jobs.dto;

import com.kanto_jobs.kanto_jobs.domain.RateType;
import lombok.*;

import java.util.List;

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
    private Boolean isNegotiable;
    private List<String> availableDays;
    private RateType rateType;
    private Boolean isBusinessMerchant;
    private String merchantBusinessName;
    private Boolean unlimitedGigsterScreening;
    private Boolean merchantPartnershipActive;
}