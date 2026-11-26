package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "appeals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Appeal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Report report;
    @ManyToOne
    private User appellant;
    @Column(length = 2000)
    private String reason;
    @ElementCollection
    @CollectionTable(name = "appeal_evidence", joinColumns = @JoinColumn(name = "appeal_id"))
    private List<AppealEvidence> evidence;
    @Enumerated(EnumType.STRING)
    private AppealStatus status = AppealStatus.PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime reviewedAt;
}