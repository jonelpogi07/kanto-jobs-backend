package com.kanto_jobs.kanto_jobs.dto;

import com.kanto_jobs.kanto_jobs.domain.RateType;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private Double lat;
    private Double lng;
    private String profileImage;
    private String bio;
    private List<String> skills;
    private List<String> availableDays;
    private RateType rateType;
    private Boolean isNegotiable;
}