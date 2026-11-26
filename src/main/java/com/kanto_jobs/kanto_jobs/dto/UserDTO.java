package com.kanto_jobs.kanto_jobs.dto;

import com.kanto_jobs.kanto_jobs.domain.RateType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String profileImage;
    private String bio;
    private Integer gigCoins;
    private LocalDateTime createdAt;
    private ContactInfoDTO contact;
    private String viberNumber;
    private String messengerUsername;
    private Long merchantId;
    private String merchantName;
    private Boolean isBusinessMerchant;
    private String merchantBusinessName;
    private Boolean unlimitedGigsterScreening;
    private Boolean merchantPartnershipActive;
    private RateType rateType;
    private List<String> skills;
    private List<String> availableDays;
    private Integer completedGigs;
    private Boolean isNegotiable;
    private Boolean phoneVerified;
}