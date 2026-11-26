package com.kanto_jobs.kanto_jobs.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long threadId;
    @ManyToOne
    private User sender;
    @Column(length = 2000)
    private String body;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime readAt;
}