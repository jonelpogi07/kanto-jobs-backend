package com.kanto_jobs.kanto_jobs.repository;

import com.kanto_jobs.kanto_jobs.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByThreadIdOrderByCreatedAtAsc(Long threadId);
    List<ChatMessage> findByThreadIdAndIdGreaterThan(Long threadId, Long afterId);
}