package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SearchNearbyGigstersResponse {
    private List<NearbyGigsterDTO> gigsters;
    private Integer count;
}