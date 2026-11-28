package com.kanto_jobs.kanto_jobs.dto;

import com.kanto_jobs.kanto_jobs.domain.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateJobPostRequest {
    @NotBlank
    @Size(max = 200)
    private String title;
    
    @NotBlank
    @Size(max = 2000)
    private String description;
    
    @NotNull
    private JobCategory category;
    
    @NotNull
    private CompensationType compensationType;
    
    @NotNull
    @DecimalMin("0.01")
    private Double amount;
    
    @NotEmpty
    private List<PaymentMethod> paymentMethods;
    
    @NotBlank
    private String barangay;
    
    @NotBlank
    private String city;
    
    private String landmark;
    
    @NotNull
    private JobTiming timing;
    
    private LocalDateTime scheduledAt;
    
    private String estimatedDuration;
    
    @jakarta.validation.constraints.Size(max = 4)
    private List<String> images;
    
    private Boolean isNegotiable;
    
    private String partnerMerchantId;
    
    private String partnerMerchantName;
    
    private Boolean isMerchantGig;
    
    private Boolean isCustomerOrderDelivery;
    
    private Long merchantId;
    
    private String merchantName;
    
    private Boolean merchantBoosted;
}