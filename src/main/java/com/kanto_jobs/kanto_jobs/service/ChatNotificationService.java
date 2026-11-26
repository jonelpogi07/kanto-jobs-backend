package com.kanto_jobs.kanto_jobs.service;

import com.kanto_jobs.kanto_jobs.domain.*;
import com.kanto_jobs.kanto_jobs.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Posts system notification cards (report / appeal) into the chat thread that
 * belongs to a report. Report threads use a negative id (-reportId) so they do
 * not collide with application threads.
 */
@Service
@RequiredArgsConstructor
public class ChatNotificationService {
    private final ChatMessageRepository msgRepo;

    @Transactional
    public void postReportNotice(Report report, String reporterRole) {
        if (report.getApplication() == null) return; // no thread to post into
        JobPost job = report.getJob();
        User target = report.getTargetUser();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("reportId", report.getId());
        data.put("jobId", job != null ? job.getId() : null);
        data.put("jobTitle", job != null ? job.getTitle() : null);
        data.put("reporterId", report.getReporter() != null ? report.getReporter().getId() : null);
        data.put("targetUserId", target != null ? target.getId() : null);
        data.put("reporterRole", reporterRole);
        data.put("status", report.getStatus() != null ? report.getStatus().name() : "PENDING");
        data.put("appealDeadline", report.getAppealDeadline() != null ? report.getAppealDeadline().toString() : null);
        data.put("reason", report.getReason());
        data.put("note", report.getNote());

        String body = "Report filed: " + (report.getReason() == null ? "No-show reported" : report.getReason())
            + ". The reported party has 24 hours to appeal.";
        save(-report.getId(), body, "report", data);
    }

    @Transactional
    public void postAppealNotice(Appeal appeal, Report report) {
        if (report.getApplication() == null) return;
        JobPost job = report.getJob();
        User appellant = appeal.getAppellant();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("appealId", appeal.getId());
        data.put("reportId", report.getId());
        data.put("jobId", job != null ? job.getId() : null);
        data.put("jobTitle", job != null ? job.getTitle() : null);
        data.put("appellantId", appellant != null ? appellant.getId() : null);
        data.put("status", appeal.getStatus() != null ? appeal.getStatus().name() : "PENDING");
        data.put("reason", appeal.getReason());
        data.put("evidenceCount", appeal.getEvidence() != null ? appeal.getEvidence().size() : 0);

        String role = appellant != null && job != null && appellant.getId().equals(job.getPoster().getId())
            ? "the client" : "the gigster";
        String body = "Appeal submitted by " + role + " — Under Review.";
        save(-report.getId(), body, "appeal", data);
    }

    private void save(Long threadId, String body, String type, Map<String, Object> data) {
        ChatMessage msg = ChatMessage.builder()
            .threadId(threadId)
            .sender(null)
            .body(body)
            .type(type)
            .data(toJson(data))
            .createdAt(LocalDateTime.now())
            .build();
        msgRepo.save(msg);
    }

    private static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(escape(e.getKey())).append('"').append(':');
            Object v = e.getValue();
            if (v == null) sb.append("null");
            else if (v instanceof Number) sb.append(v);
            else sb.append('"').append(escape(String.valueOf(v))).append('"');
        }
        return sb.append('}').toString();
    }

    private static String escape(String s) {
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default: out.append(c);
            }
        }
        return out.toString();
    }
}