package com.kanto_jobs.kanto_jobs.dto;

import com.kanto_jobs.kanto_jobs.domain.JobStatus;
import com.kanto_jobs.kanto_jobs.domain.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatThreadDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private JobStatus jobStatus;
    private Long applicationId;
    private ApplicationStatus applicationStatus;
    private Long posterId;
    private UserSummaryDTO otherUser;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;
    private LocalDateTime updatedAt;
}