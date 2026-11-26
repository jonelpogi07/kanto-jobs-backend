package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactInfoDTO {
    private String phoneNumber;
    private String viberNumber;
    private String messengerUsername;
}