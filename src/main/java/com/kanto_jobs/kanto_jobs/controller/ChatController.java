package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.common.CurrentUser;
import com.kanto_jobs.kanto_jobs.domain.*;
import com.kanto_jobs.kanto_jobs.dto.*;
import com.kanto_jobs.kanto_jobs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageRepository msgRepo;
    private final ApplicationRepository appRepo;
    private final JobPostRepository jobRepo;
    private final CurrentUser currentUser;

    @GetMapping("/threads")
    public List<ChatThreadDTO> threads() {
        User u = currentUser.get();
        return appRepo.findAll().stream().map(a -> {
            JobPost j = a.getJob();
            User other = a.getApplicant().getId().equals(u.getId()) ? j.getPoster() : a.getApplicant();
            List<ChatMessage> msgs = msgRepo.findByThreadIdOrderByCreatedAtAsc(a.getId());
            ChatMessage last = msgs.isEmpty() ? null : msgs.get(msgs.size()-1);
            ChatThreadDTO t = new ChatThreadDTO();
            t.setId(a.getId()); t.setJobId(j.getId()); t.setJobTitle(j.getTitle()); t.setJobStatus(j.getStatus());
            t.setApplicationId(a.getId()); t.setApplicationStatus(a.getStatus()); t.setPosterId(j.getPoster().getId());
            UserSummaryDTO ou = new UserSummaryDTO();
            ou.setId(other.getId()); ou.setFullName(other.getFirstName()+" "+other.getLastName());
            ou.setAvatarUrl(other.getProfileImage()); ou.setBarangay(other.getAddress()); ou.setCity(other.getAddress());
            ou.setPhoneVerified(false); ou.setRatingAverage(0.0); ou.setRatingCount(0); ou.setCompletedGigs(0);
            t.setOtherUser(ou);
            t.setLastMessage(last != null ? last.getBody() : null);
            t.setLastMessageAt(last != null ? last.getCreatedAt() : null);
            t.setUnreadCount(0);
            t.setUpdatedAt(a.getCreatedAt());
            return t;
        }).collect(Collectors.toList());
    }

    @GetMapping("/threads/{threadId}/messages")
    public List<ChatMessageDTO> messages(@PathVariable Long threadId, @RequestParam(required = false) Long afterId) {
        List<ChatMessage> list = afterId != null
            ? msgRepo.findByThreadIdAndIdGreaterThan(threadId, afterId)
            : msgRepo.findByThreadIdOrderByCreatedAtAsc(threadId);
        return list.stream().map(m -> {
            ChatMessageDTO d = new ChatMessageDTO();
            d.setId(m.getId()); d.setThreadId(m.getThreadId()); d.setSenderId(m.getSender().getId());
            d.setBody(m.getBody()); d.setCreatedAt(m.getCreatedAt()); d.setReadAt(m.getReadAt()); d.setType("text");
            return d;
        }).collect(Collectors.toList());
    }

    @PostMapping("/threads/{threadId}/messages")
    public ChatMessageDTO send(@PathVariable Long threadId, @RequestBody java.util.Map<String,String> body) {
        User u = currentUser.get();
        ChatMessage m = ChatMessage.builder().threadId(threadId).sender(u).body(body.get("body")).build();
        msgRepo.save(m);
        ChatMessageDTO d = new ChatMessageDTO();
        d.setId(m.getId()); d.setThreadId(m.getThreadId()); d.setSenderId(u.getId());
        d.setBody(m.getBody()); d.setCreatedAt(m.getCreatedAt()); d.setType("text");
        return d;
    }
}
