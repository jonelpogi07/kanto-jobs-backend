package com.kanto_jobs.kanto_jobs.service;

import com.kanto_jobs.kanto_jobs.domain.*;
import com.kanto_jobs.kanto_jobs.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigCoinRefundService {
    private final ReportRepository reportRepo;
    private final AppealRepository appealRepo;
    private final UserRepository userRepo;
    private final JobPostRepository jobRepo;
    private final ApplicationRepository applicationRepo;

    @Scheduled(fixedRate = 3600000) // Run every hour
    @Transactional
    public void evaluateGigCoinRefunds() {
        log.info("Evaluating GigCoin refunds for expired appeals...");
        LocalDateTime now = LocalDateTime.now();
        
        // Find reports where appeal deadline has passed and no appeal was filed, or appeal was rejected
        List<Report> pendingReports = reportRepo.findAll().stream()
            .filter(r -> r.getStatus() == ReportStatus.PENDING && r.getAppealDeadline() != null && r.getAppealDeadline().isBefore(now))
            .toList();
        
        for (Report report : pendingReports) {
            processRefund(report);
        }
        
        // Also check appealed reports where appeal was rejected
        List<Report> appealedReports = reportRepo.findAll().stream()
            .filter(r -> r.getStatus() == ReportStatus.APPEALED && r.getAppealDeadline() != null && r.getAppealDeadline().isBefore(now))
            .toList();
        
        for (Report report : appealedReports) {
            // Check if appeal was rejected
            Appeal appeal = appealRepo.findByReportId(report.getId()).orElse(null);
            if (appeal != null && appeal.getStatus() == AppealStatus.REJECTED) {
                processRefund(report);
            }
        }
    }
    
    private void processRefund(Report report) {
        if (Boolean.TRUE.equals(report.getRefunded())) {
            return; // Already refunded
        }
        
        // Determine who gets the refund based on who reported
        // If client reported gigster no-show, refund goes to client
        // If gigster reported client no-show, refund goes to gigster
        User reporter = report.getReporter();
        if (reporter != null) {
            reporter.setGigCoins((reporter.getGigCoins() == null ? 0 : reporter.getGigCoins()) + 1);
            userRepo.save(reporter);
            log.info("Refunded 1 GigCoin to user {} for report {}", reporter.getId(), report.getId());
        }
        
        report.setRefunded(true);
        report.setRefundedAt(LocalDateTime.now());
        report.setStatus(ReportStatus.REFUNDED);
        reportRepo.save(report);
        
        // Update job/application gigCoinRefunded flag
        if (report.getJob() != null) {
            JobPost job = report.getJob();
            job.setGigCoinRefunded(true);
            job.setRefundedAt(LocalDateTime.now());
            jobRepo.save(job);
        }
        
        if (report.getApplication() != null) {
            Application app = report.getApplication();
            // Application doesn't have gigCoinRefunded field, but job does
        }
        
        // Create system message in chat thread
        if (report.getApplication() != null) {
            Long threadId = -report.getId(); // Report thread uses negative ID
            ChatMessage systemMsg = ChatMessage.builder()
                .threadId(threadId)
                .sender(null) // System message
                .body("GigCoin refunded: 1 GigCoin has been refunded to " + reporter.getFirstName() + " " + reporter.getLastName())
                .build();
            // Note: ChatMessageRepository.save would need sender to be nullable
        }
    }
}