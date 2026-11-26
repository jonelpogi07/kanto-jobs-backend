package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appeals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Appeal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reportId;
    private Long appellantId;
    @Column(length = 2000)
    private String reason;
    @Enumerated(EnumType.STRING)
    private AppealStatus status = AppealStatus.PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime reviewedAt;
}