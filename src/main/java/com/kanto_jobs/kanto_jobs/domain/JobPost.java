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
    @Enumerated(EnumType.STRING)
    private JobTiming timing;
    private LocalDateTime scheduledAt;
    private String estimatedDuration;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JobStatus status = JobStatus.OPEN;
    @ManyToOne
    private User poster;
    @Builder.Default
    private Integer applicantCount = 0;
    private Long acceptedApplicationId;
    @ElementCollection
    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private List<String> images;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Boolean isNegotiable;
    private String partnerMerchantId;
    private String partnerMerchantName;
    private Boolean isMerchantGig;
    private Boolean isCustomerOrderDelivery;
    private Long merchantId;
    private String merchantName;
    private Boolean merchantBoosted;
    private Long reportId;
    private LocalDateTime reportedAt;
    private LocalDateTime appealDeadline;
    private Boolean gigCoinRefunded;
    private LocalDateTime refundedAt;
}