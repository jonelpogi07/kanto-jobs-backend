package com.kanto_jobs.kanto_jobs.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateReviewRequest {
    @NotNull @Min(1) @Max(5) private Integer rating;
    @Size(max = 2000) private String comment;
}