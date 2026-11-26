package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.common.CurrentUser;
import com.kanto_jobs.kanto_jobs.domain.*;
import com.kanto_jobs.kanto_jobs.dto.*;
import com.kanto_jobs.kanto_jobs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationRepository appRepo;
    private final JobPostRepository jobRepo;
    private final UserRepository userRepo;
    private final CurrentUser currentUser;

    @GetMapping("/mine")
    public List<ApplicationDTO> mine() {
        Long uid = currentUser.get().getId();
        return appRepo.findByApplicantId(uid).stream().map(ApplicationController::toDto).collect(Collectors.toList());
    }

    @PostMapping("/{id}/screen")
    public ApplicationDTO screen(@PathVariable Long id) {
        Application a = appRepo.findById(id).orElseThrow();
        a.setStatus(ApplicationStatus.SCREENING); appRepo.save(a);
        return toDto(a);
    }

    @PostMapping("/{id}/accept")
    public ApplicationDTO accept(@PathVariable Long id) {
        Application a = appRepo.findById(id).orElseThrow();
        a.setStatus(ApplicationStatus.ACCEPTED); appRepo.save(a);
        JobPost j = a.getJob(); j.setAcceptedApplicationId(a.getId()); j.setStatus(JobStatus.IN_PROGRESS); jobRepo.save(j);
        return toDto(a);
    }

    @PostMapping("/{id}/decline")
    public ApplicationDTO decline(@PathVariable Long id) {
        Application a = appRepo.findById(id).orElseThrow();
        a.setStatus(ApplicationStatus.REJECTED); appRepo.save(a);
        return toDto(a);
    }

    @PostMapping("/{id}/cancel")
    public ApplicationDTO cancel(@PathVariable Long id) {
        Application a = appRepo.findById(id).orElseThrow();
        a.setStatus(ApplicationStatus.CANCELLED); appRepo.save(a);
        return toDto(a);
    }

    public static ApplicationDTO toDtoStatic(Application a) { return toDto(a); }

    private static ApplicationDTO toDto(Application a) {
        User u = a.getApplicant();
        ApplicantDTO app = new ApplicantDTO();
        app.setId(u.getId());
        app.setFullName(u.getFirstName()+" "+u.getLastName());
        app.setAvatarUrl(u.getProfileImage());
        app.setBarangay(u.getAddress());
        app.setCity(u.getAddress());
        app.setPhoneVerified(false);
        app.setRatingAverage(0.0);
        app.setRatingCount(0);
        app.setCompletedGigs(0);
        app.setSkills(List.of());
        JobPostDTO job = null;
        try { job = null; } catch (Exception ignored) {}
        return ApplicationDTO.builder().id(a.getId()).jobId(a.getJob().getId()).job(job)
            .applicant(app).pitch(a.getPitch()).status(a.getStatus()).createdAt(a.getCreatedAt()).build();
    }
}
