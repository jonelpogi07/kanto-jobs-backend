package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

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
}