package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.common.CurrentUser;
import com.kanto_jobs.kanto_jobs.domain.*;
import com.kanto_jobs.kanto_jobs.dto.*;
import com.kanto_jobs.kanto_jobs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppealController {
    private final ReportRepository reportRepo;
    private final AppealRepository appealRepo;
    private final CurrentUser currentUser;

    @PostMapping("/reports/{reportId}/appeal")
    public AppealDTO appeal(@PathVariable Long reportId, @RequestBody java.util.Map<String,String> body) {
        Report r = reportRepo.findById(reportId).orElseThrow();
        User u = currentUser.get();
        r.setStatus(ReportStatus.APPEALED);
        r.setAppealedAt(LocalDateTime.now());
        reportRepo.save(r);
        Appeal a = Appeal.builder().reportId(reportId).appellantId(u.getId()).reason(body.get("reason")).build();
        appealRepo.save(a);
        AppealDTO d = new AppealDTO();
        d.setId(a.getId()); d.setReportId(a.getReportId()); d.setAppellantId(a.getAppellantId());
        d.setReason(a.getReason()); d.setStatus(a.getStatus()); d.setCreatedAt(a.getCreatedAt()); d.setReviewedAt(a.getReviewedAt());
        return d;
    }
}