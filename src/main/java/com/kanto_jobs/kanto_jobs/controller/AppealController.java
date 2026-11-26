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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppealController {
    private final ReportRepository reportRepo;
    private final AppealRepository appealRepo;
    private final CurrentUser currentUser;
    private final com.kanto_jobs.kanto_jobs.service.ChatNotificationService chatNotificationService;

    @PostMapping("/reports/{reportId}/appeal")
    public AppealDTO appeal(@PathVariable Long reportId, @RequestBody CreateAppealRequest req) {
        Report r = reportRepo.findById(reportId).orElseThrow();
        User u = currentUser.get();
        r.setStatus(ReportStatus.APPEALED);
        r.setAppealedAt(LocalDateTime.now());
        reportRepo.save(r);
        List<AppealEvidence> evidence = req.getEvidence() != null ? req.getEvidence().stream().map(dto -> {
            AppealEvidence e = new AppealEvidence();
            e.setId(dto.getId());
            e.setName(dto.getName());
            e.setType(dto.getType());
            e.setSize(dto.getSize());
            e.setDataUrl(dto.getDataUrl());
            return e;
        }).collect(Collectors.toList()) : null;
        
        Appeal a = Appeal.builder()
            .report(r)
            .appellant(u)
            .reason(req.getReason())
            .evidence(evidence)
            .build();
        appealRepo.save(a);
        chatNotificationService.postAppealNotice(a, r);
        return toDto(a);
    }

    private AppealDTO toDto(Appeal a) {
        AppealDTO d = new AppealDTO();
        d.setId(a.getId());
        d.setReportId(a.getReport().getId());
        d.setAppellantId(a.getAppellant().getId());
        d.setReason(a.getReason());
        d.setStatus(a.getStatus());
        d.setCreatedAt(a.getCreatedAt());
        d.setReviewedAt(a.getReviewedAt());
        if (a.getEvidence() != null) {
            d.setEvidence(a.getEvidence().stream().map(e -> {
                AppealEvidenceDTO dto = new AppealEvidenceDTO();
                dto.setId(e.getId());
                dto.setName(e.getName());
                dto.setType(e.getType());
                dto.setSize(e.getSize());
                dto.setDataUrl(e.getDataUrl());
                return dto;
            }).collect(Collectors.toList()));
        }
        return d;
    }
}