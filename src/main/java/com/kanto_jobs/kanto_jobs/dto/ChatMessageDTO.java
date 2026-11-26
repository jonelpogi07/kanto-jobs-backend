package com.kanto_jobs.kanto_jobs.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageDTO {
    private Long id;
    private Long threadId;
    private Long senderId;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private String type;
}