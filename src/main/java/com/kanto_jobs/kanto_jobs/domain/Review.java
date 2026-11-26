package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long jobId;
    @ManyToOne
    private User reviewer;
    private Long revieweeId;
    private Integer rating;
    @Column(length = 2000)
    private String comment;
    private LocalDateTime createdAt = LocalDateTime.now();
}