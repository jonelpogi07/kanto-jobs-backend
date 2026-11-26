package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewDTO {
    private Long id;
    private Long jobId;
    private UserSummaryDTO reviewer;
    private Long revieweeId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}