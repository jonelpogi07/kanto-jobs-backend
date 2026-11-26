package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User reporter;
    @ManyToOne
    private User targetUser;
    @ManyToOne
    private JobPost job;
    @ManyToOne
    private Application application;
    private String reason;
    private String note;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;
    @Builder.Default
    private LocalDateTime reportedAt = LocalDateTime.now();
    private LocalDateTime appealDeadline;
    private LocalDateTime appealedAt;
    @Builder.Default
    private Boolean refunded = false;
    private LocalDateTime refundedAt;
}