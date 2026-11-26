package com.kanto_jobs.kanto_jobs.dto;

import com.kanto_jobs.kanto_jobs.domain.JobCategory;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SearchNearbyGigstersRequest {
    private Double latitude;
    private Double longitude;
    private Double maxDistanceKm;
    private JobCategory category;
}