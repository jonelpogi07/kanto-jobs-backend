package com.kanto_jobs.kanto_jobs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    @Email @NotBlank private String email;
    @NotBlank private String password;
    private String firstName;
    private String lastName;
}