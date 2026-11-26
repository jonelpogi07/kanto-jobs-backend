package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 2000)
    private String description;
    @Enumerated(EnumType.STRING)
    private JobCategory category;
    @Enumerated(EnumType.STRING)
    private CompensationType compensationType;
    private Double amount;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<PaymentMethod> paymentMethods;
    private String barangay;
    private String city;
    private String landmark;
    private Double latitude;
    private Double longitude;
    @Enumerated(EnumType.STRING)
    private JobTiming timing;
    private LocalDateTime scheduledAt;
    private String estimatedDuration;
    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.OPEN;
    @ManyToOne
    private User poster;
    private Integer applicantCount = 0;
    private Long acceptedApplicationId;
    @ElementCollection
    private List<String> images;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Boolean isNegotiable;
    private String partnerMerchantId;
    private String partnerMerchantName;
    private Boolean isMerchantGig;
    private Boolean isCustomerOrderDelivery;
    private Long merchantId;
    private String merchantName;
    private Boolean merchantBoosted;
}