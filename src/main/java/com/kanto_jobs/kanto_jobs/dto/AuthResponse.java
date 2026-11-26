package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserDTO user;
}