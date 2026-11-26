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
    private String phone;
    private String address;
    private Double lat;
    private Double lng;
    private String profileImage;
    private String bio;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    private Integer gigCoins = 0;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "phoneNumber", column = @Column(name = "contact_phone_number")),
        @AttributeOverride(name = "viberNumber", column = @Column(name = "contact_viber_number")),
        @AttributeOverride(name = "messengerUsername", column = @Column(name = "contact_messenger_username"))
    })
    private ContactInfo contact;

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

    private Integer completedGigs = 0;
    private Boolean isNegotiable;
    private Boolean phoneVerified;

    private Double ratingAverage = 0.0;
    private Integer ratingCount = 0;
}