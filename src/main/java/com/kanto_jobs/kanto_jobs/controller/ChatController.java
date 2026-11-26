package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.common.CurrentUser;
import com.kanto_jobs.kanto_jobs.domain.*;
import com.kanto_jobs.kanto_jobs.dto.*;
import com.kanto_jobs.kanto_jobs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageRepository msgRepo;
    private final ApplicationRepository appRepo;
    private final JobPostRepository jobRepo;
    private final ReportRepository reportRepo;
    private final AppealRepository appealRepo;
    private final CurrentUser currentUser;

    @GetMapping("/threads")
    public List<ChatThreadDTO> threads() {
        User u = currentUser.get();
        List<ChatThreadDTO> threads = new ArrayList<>();
        
        // Application-based threads
        List<Application> applications = appRepo.findAll();
        for (Application a : applications) {
            JobPost j = a.getJob();
            boolean isParticipant = a.getApplicant().getId().equals(u.getId()) || j.getPoster().getId().equals(u.getId());
            if (!isParticipant) continue;
            
            User other = a.getApplicant().getId().equals(u.getId()) ? j.getPoster() : a.getApplicant();
            List<ChatMessage> msgs = msgRepo.findByThreadIdOrderByCreatedAtAsc(a.getId());
            ChatMessage last = msgs.isEmpty() ? null : msgs.get(msgs.size()-1);
            ChatThreadDTO t = new ChatThreadDTO();
            t.setId(a.getId()); 
            t.setJobId(j.getId()); 
            t.setJobTitle(j.getTitle()); 
            t.setJobStatus(j.getStatus());
            t.setApplicationId(a.getId()); 
            t.setApplicationStatus(a.getStatus()); 
            t.setPosterId(j.getPoster().getId());
            UserSummaryDTO ou = new UserSummaryDTO();
            ou.setId(other.getId()); 
            ou.setFullName(other.getFirstName()+" "+other.getLastName());
            ou.setAvatarUrl(other.getProfileImage()); 
            ou.setBarangay(other.getAddress()); 
            ou.setCity(other.getAddress());
            ou.setPhoneVerified(false); 
            ou.setRatingAverage(0.0); 
            ou.setRatingCount(0); 
            ou.setCompletedGigs(0);
            t.setOtherUser(ou);
            t.setLastMessage(last != null ? last.getBody() : null);
            t.setLastMessageAt(last != null ? last.getCreatedAt() : null);
            t.setUnreadCount((int) msgs.stream().filter(m -> m.getReadAt() == null && (m.getSender() == null || !m.getSender().getId().equals(u.getId()))).count());
            t.setUpdatedAt(a.getCreatedAt());
            t.setHasReportNotice(a.getReportId() != null);
            threads.add(t);
        }
        
        // Report/Appeal system threads (negative IDs)
        // Reports where user is reporter or target
        List<Report> userReports = reportRepo.findAll().stream()
            .filter(r -> r.getReporter().getId().equals(u.getId()) || r.getTargetUser().getId().equals(u.getId()))
            .toList();
        
        for (Report r : userReports) {
            if (r.getApplication() != null) {
                // Check if we already have this thread
                boolean exists = threads.stream().anyMatch(t -> t.getId() != null && t.getId().equals(-r.getId()));
                if (!exists) {
                    Application a = r.getApplication();
                    JobPost j = a.getJob();
                    User other = r.getReporter().getId().equals(u.getId()) ? r.getTargetUser() : r.getReporter();
                    List<ChatMessage> msgs = msgRepo.findByThreadIdOrderByCreatedAtAsc(-r.getId());
                    ChatMessage last = msgs.isEmpty() ? null : msgs.get(msgs.size()-1);
                    ChatThreadDTO t = new ChatThreadDTO();
                    t.setId(-r.getId()); // Negative ID for report threads
                    t.setJobId(j.getId()); 
                    t.setJobTitle(j.getTitle()); 
                    t.setJobStatus(j.getStatus());
                    t.setApplicationId(a.getId()); 
                    t.setApplicationStatus(a.getStatus()); 
                    t.setPosterId(j.getPoster().getId());
                    UserSummaryDTO ou = new UserSummaryDTO();
                    ou.setId(other.getId()); 
                    ou.setFullName(other.getFirstName()+" "+other.getLastName());
                    ou.setAvatarUrl(other.getProfileImage()); 
                    ou.setBarangay(other.getAddress()); 
                    ou.setCity(other.getAddress());
                    ou.setPhoneVerified(false); 
                    ou.setRatingAverage(0.0); 
                    ou.setRatingCount(0); 
                    ou.setCompletedGigs(0);
                    t.setOtherUser(ou);
                    t.setLastMessage(last != null ? last.getBody() : null);
                    t.setLastMessageAt(last != null ? last.getCreatedAt() : null);
                    t.setUnreadCount((int) msgs.stream().filter(m -> m.getReadAt() == null && (m.getSender() == null || !m.getSender().getId().equals(u.getId()))).count());
                    t.setUpdatedAt(r.getReportedAt());
                    t.setHasReportNotice(true);
                    threads.add(t);
                }
            }
        }
        
        return threads;
    }

    @GetMapping("/threads/{threadId}/messages")
    public List<ChatMessageDTO> messages(@PathVariable Long threadId, @RequestParam(required = false) Long afterId) {
        List<ChatMessage> list = afterId != null
            ? msgRepo.findByThreadIdAndIdGreaterThan(threadId, afterId)
            : msgRepo.findByThreadIdOrderByCreatedAtAsc(threadId);
        return list.stream().map(m -> {
            ChatMessageDTO d = new ChatMessageDTO();
            d.setId(m.getId()); 
            d.setThreadId(m.getThreadId()); 
            d.setSenderId(m.getSender() != null ? m.getSender().getId() : 0L);
            d.setBody(m.getBody()); 
            d.setCreatedAt(m.getCreatedAt()); 
            d.setReadAt(m.getReadAt()); 
            d.setType(m.getType() != null ? m.getType() : "text");
            d.setData(m.getData());
            return d;
        }).collect(Collectors.toList());
    }

    @PostMapping("/threads/{threadId}/messages")
    public ChatMessageDTO send(@PathVariable Long threadId, @RequestBody java.util.Map<String,String> body) {
        User u = currentUser.get();
        
        // Check if user is participant in this thread
        boolean isParticipant = false;
        if (threadId > 0) {
            // Application thread
            Application a = appRepo.findById(threadId).orElseThrow();
            JobPost j = a.getJob();
            isParticipant = a.getApplicant().getId().equals(u.getId()) || j.getPoster().getId().equals(u.getId());
            
            // Check gating: only allow if application is SCREENING or ACCEPTED
            if (isParticipant && a.getStatus() != ApplicationStatus.SCREENING && a.getStatus() != ApplicationStatus.ACCEPTED) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Chat only available when application is SCREENING or ACCEPTED");
            }
        } else {
            // Report/Appeal thread (negative ID)
            Long reportId = -threadId;
            Report r = reportRepo.findById(reportId).orElseThrow();
            isParticipant = r.getReporter().getId().equals(u.getId()) || r.getTargetUser().getId().equals(u.getId());
        }
        
        if (!isParticipant) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Not a participant in this thread");
        }
        
        ChatMessage m = ChatMessage.builder().threadId(threadId).sender(u).body(body.get("body")).build();
        msgRepo.save(m);
        ChatMessageDTO d = new ChatMessageDTO();
        d.setId(m.getId()); 
        d.setThreadId(m.getThreadId()); 
        d.setSenderId(u.getId());
        d.setBody(m.getBody()); 
        d.setCreatedAt(m.getCreatedAt()); 
        d.setType("text");
        return d;
    }

    @PostMapping("/threads/{threadId}/read")
    public void markAsRead(@PathVariable Long threadId, @RequestBody(required = false) java.util.Map<String, Long> body) {
        User u = currentUser.get();
        Long lastReadId = body != null ? body.get("lastReadId") : null;
        
        if (lastReadId != null) {
            List<ChatMessage> unreadMessages = msgRepo.findByThreadIdAndIdGreaterThan(threadId, 0L)
                .stream()
                .filter(m -> m.getId() <= lastReadId && m.getReadAt() == null
                    && (m.getSender() == null || !m.getSender().getId().equals(u.getId())))
                .toList();
            for (ChatMessage m : unreadMessages) {
                m.setReadAt(LocalDateTime.now());
            }
            msgRepo.saveAll(unreadMessages);
        }
    }
}
