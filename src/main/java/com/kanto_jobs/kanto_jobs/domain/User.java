package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
}