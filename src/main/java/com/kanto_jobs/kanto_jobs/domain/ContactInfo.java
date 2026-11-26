package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactInfo {
    private String phoneNumber;
    private String viberNumber;
    private String messengerUsername;
}