package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Application {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private JobPost job;
    @ManyToOne
    private User applicant;
    @Column(length = 2000)
    private String pitch;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();
}