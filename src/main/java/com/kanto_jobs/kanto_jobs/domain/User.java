package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String phone;
    private String address;
    @Column(columnDefinition = "TEXT")
    private String profileImage;
    private String bio;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
    @Builder.Default
    private Integer gigCoins = 0;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    private String viberNumber;
    private String messengerUsername;

    private Long merchantId;
    private String merchantName;
    private Boolean isBusinessMerchant;
    private String merchantBusinessName;
    private Boolean unlimitedGigsterScreening;
    private Boolean merchantPartnershipActive;

    @Enumerated(EnumType.STRING)
    private RateType rateType;

    @ElementCollection
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    private List<String> skills;

    @ElementCollection
    @CollectionTable(name = "user_available_days", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "day")
    private List<String> availableDays;

    @Builder.Default
    private Integer completedGigs = 0;
    private Boolean isNegotiable;
    private Boolean phoneVerified;

    @Builder.Default
    private Double ratingAverage = 0.0;
    @Builder.Default
    private Integer ratingCount = 0;
}