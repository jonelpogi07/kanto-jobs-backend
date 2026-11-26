package com.kanto_jobs.kanto_jobs.dto;

import com.kanto_jobs.kanto_jobs.domain.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobPostDTO {
    private Long id;
    private String title;
    private String description;
    private JobCategory category;
    private CompensationType compensationType;
    private Double amount;
    private List<PaymentMethod> paymentMethods;
    private String barangay;
    private String city;
    private String landmark;
    private JobTiming timing;
    private LocalDateTime scheduledAt;
    private String estimatedDuration;
    private JobStatus status;
    private UserSummaryDTO poster;
    private Integer applicantCount;
    private Long acceptedApplicationId;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isNegotiable;
    private String partnerMerchantId;
    private String partnerMerchantName;
    private Boolean isMerchantGig;
    private Boolean isCustomerOrderDelivery;
    private Long merchantId;
    private String merchantName;
    private Boolean merchantBoosted;
    private Boolean reportedByMe;
}