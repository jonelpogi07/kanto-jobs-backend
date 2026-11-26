package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.common.CurrentUser;
import com.kanto_jobs.kanto_jobs.domain.*;
import com.kanto_jobs.kanto_jobs.dto.*;
import com.kanto_jobs.kanto_jobs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportRepository reportRepo;
    private final JobPostRepository jobRepo;
    private final ApplicationRepository applicationRepo;
    private final CurrentUser currentUser;

    @GetMapping
    public Page<ReportDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User u = currentUser.get();
        List<Report> reports = reportRepo.findAll().stream()
            .filter(r -> r.getReporter().getId().equals(u.getId()) || r.getTargetUser().getId().equals(u.getId()))
            .toList();
        PageRequest pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, reports.size());
        List<ReportDTO> slice = start >= reports.size() ? List.of() : 
            reports.subList(start, end).stream().map(this::toDto).collect(Collectors.toList());
        return new org.springframework.data.domain.PageImpl<>(slice, pageable, reports.size());
    }

    @GetMapping("/{id}")
    public ReportDTO get(@PathVariable Long id) {
        Report r = reportRepo.findById(id).orElseThrow();
        User u = currentUser.get();
        if (!r.getReporter().getId().equals(u.getId()) && !r.getTargetUser().getId().equals(u.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Not authorized to view this report");
        }
        return toDto(r);
    }

    @PostMapping
    public ReportDTO create(@RequestBody CreateReportRequest req) {
        User u = currentUser.get();
        JobPost job = jobRepo.findById(req.getJobId()).orElseThrow();
        Application application = null;
        User targetUser = null;
        
        if (req.getApplicationId() != null) {
            application = applicationRepo.findById(req.getApplicationId()).orElseThrow();
            targetUser = application.getApplicant().getId().equals(u.getId()) ? job.getPoster() : application.getApplicant();
        } else {
            targetUser = job.getPoster();
        }
        
        Report r = Report.builder()
            .reporter(u)
            .targetUser(targetUser)
            .job(job)
            .application(application)
            .reason(req.getReason())
            .note(req.getNote())
            .status(ReportStatus.PENDING)
            .reportedAt(LocalDateTime.now())
            .appealDeadline(LocalDateTime.now().plusHours(24))
            .build();
        reportRepo.save(r);
        
        // Update job/application with report info
        job.setReportId(r.getId());
        job.setReportedAt(LocalDateTime.now());
        job.setAppealDeadline(LocalDateTime.now().plusHours(24));
        jobRepo.save(job);
        
        if (application != null) {
            application.setReportId(r.getId());
            applicationRepo.save(application);
        }
        
        return toDto(r);
    }

    private ReportDTO toDto(Report r) {
        ReportDTO d = new ReportDTO();
        d.setId(r.getId());
        d.setReporterId(r.getReporter().getId());
        d.setTargetUserId(r.getTargetUser().getId());
        d.setJobId(r.getJob().getId());
        d.setApplicationId(r.getApplication() != null ? r.getApplication().getId() : null);
        d.setReason(r.getReason());
        d.setNote(r.getNote());
        d.setStatus(r.getStatus());
        d.setReportedAt(r.getReportedAt());
        d.setAppealDeadline(r.getAppealDeadline());
        d.setAppealReason(r.getNote()); // Using note as appeal reason
        d.setAppealedAt(r.getAppealedAt());
        d.setRefunded(r.getRefunded());
        d.setRefundedAt(r.getRefundedAt());
        return d;
    }
}