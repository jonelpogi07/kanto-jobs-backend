package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateReviewRequest {
    private Integer rating;
    private String comment;
}