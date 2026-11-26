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
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationRepository appRepo;
    private final JobPostRepository jobRepo;
    private final UserRepository userRepo;
    private final ReportRepository reportRepo;
    private final ReviewRepository reviewRepo;
    private final CurrentUser currentUser;
    private final com.kanto_jobs.kanto_jobs.service.ChatNotificationService chatNotificationService;

    @GetMapping("/mine")
    public List<ApplicationDTO> mine() {
        Long uid = currentUser.get().getId();
        return appRepo.findByApplicantId(uid).stream().map(a -> toDto(a, userRepo, reviewRepo)).collect(Collectors.toList());
    }

    @GetMapping("/job/{jobId}")
    public List<ApplicationDTO> byJob(@PathVariable Long jobId) {
        User u = currentUser.get();
        JobPost job = jobRepo.findById(jobId).orElseThrow();
        if (!job.getPoster().getId().equals(u.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Only the poster can view applications");
        }
        return appRepo.findByJobId(jobId).stream().map(a -> toDto(a, userRepo, reviewRepo)).collect(Collectors.toList());
    }

    @PostMapping("/{id}/screen")
    public ApplicationDTO screen(@PathVariable Long id) {
        Application a = appRepo.findById(id).orElseThrow();
        a.setStatus(ApplicationStatus.SCREENING); appRepo.save(a);
        return toDto(a, userRepo, reviewRepo);
    }

    @PostMapping("/{id}/accept")
    public ApplicationDTO accept(@PathVariable Long id) {
        Application a = appRepo.findById(id).orElseThrow();
        a.setStatus(ApplicationStatus.ACCEPTED); appRepo.save(a);
        JobPost j = a.getJob(); j.setAcceptedApplicationId(a.getId()); j.setStatus(JobStatus.IN_PROGRESS); jobRepo.save(j);
        return toDto(a, userRepo, reviewRepo);
    }

    @PostMapping("/{id}/decline")
    public ApplicationDTO decline(@PathVariable Long id) {
        Application a = appRepo.findById(id).orElseThrow();
        a.setStatus(ApplicationStatus.REJECTED); appRepo.save(a);
        return toDto(a, userRepo, reviewRepo);
    }

    @PostMapping("/{id}/cancel")
    public ApplicationDTO cancel(@PathVariable Long id) {
        Application a = appRepo.findById(id).orElseThrow();
        a.setStatus(ApplicationStatus.CANCELLED); appRepo.save(a);
        return toDto(a, userRepo, reviewRepo);
    }

    @PostMapping("/{id}/invite")
    public ApplicationDTO invite(@PathVariable Long id) {
        Application a = appRepo.findById(id).orElseThrow();
        if (a.getStatus() != ApplicationStatus.PENDING) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "Can only invite pending applications");
        }
        a.setStatus(ApplicationStatus.INVITED);
        appRepo.save(a);
        return toDto(a, userRepo, reviewRepo);
    }

    @PostMapping("/{id}/respond-invitation")
    public ApplicationDTO respondInvitation(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        Application a = appRepo.findById(id).orElseThrow();
        User u = currentUser.get();
        if (!a.getApplicant().getId().equals(u.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Only the applicant can respond to invitation");
        }
        String response = body.get("response"); // "accept" or "decline"
        if ("accept".equalsIgnoreCase(response)) {
            a.setStatus(ApplicationStatus.ACCEPTED);
            JobPost j = a.getJob();
            j.setAcceptedApplicationId(a.getId());
            j.setStatus(JobStatus.IN_PROGRESS);
            jobRepo.save(j);
        } else if ("decline".equalsIgnoreCase(response)) {
            a.setStatus(ApplicationStatus.REJECTED);
        }
        appRepo.save(a);
        return toDto(a, userRepo, reviewRepo);
    }

    @PostMapping("/{id}/report-no-show")
    public ApplicationDTO reportNoShow(@PathVariable Long id) {
        Application a = appRepo.findById(id).orElseThrow();
        User u = currentUser.get();
        // Check if user is the applicant
        if (!a.getApplicant().getId().equals(u.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Only the applicant can report no-show");
        }
        // Create report
        Report r = Report.builder()
            .reporter(u)
            .targetUser(a.getJob().getPoster())
            .job(a.getJob())
            .application(a)
            .reason("No-show reported by gigster")
            .status(ReportStatus.PENDING)
            .reportedAt(LocalDateTime.now())
            .appealDeadline(LocalDateTime.now().plusHours(24))
            .build();
        reportRepo.save(r);
        // Update application with report info
        a.setReportId(r.getId());
        a.setReportedByMe(true);
        appRepo.save(a);
        chatNotificationService.postReportNotice(r, "Gigster");
        return toDto(a, userRepo, reviewRepo);
    }

    public static ApplicationDTO toDtoStatic(Application a, UserRepository userRepo, ReviewRepository reviewRepo) {
        return toDto(a, userRepo, reviewRepo);
    }

    private static ApplicationDTO toDto(Application a, UserRepository userRepo, ReviewRepository reviewRepo) {
        User u = a.getApplicant();
        ApplicantDTO app = new ApplicantDTO();
        app.setId(u.getId());
        app.setFullName(u.getFirstName()+" "+u.getLastName());
        app.setAvatarUrl(u.getProfileImage());
        app.setBarangay(u.getAddress());
        app.setCity(u.getAddress());
        app.setPhoneVerified(u.getPhoneVerified());
        app.setRatingAverage(0.0);
        app.setRatingCount(0);
        app.setCompletedGigs(u.getCompletedGigs());
        app.setSkills(u.getSkills() != null ? u.getSkills() : List.of());
        
        JobPostDTO job = null;
        try { 
            JobPost jp = a.getJob();
            if (jp != null) {
                job = new JobPostDTO();
                job.setId(jp.getId());
                job.setTitle(jp.getTitle());
                job.setCategory(jp.getCategory());
                job.setCompensationType(jp.getCompensationType());
                job.setAmount(jp.getAmount());
                job.setStatus(jp.getStatus());
                job.setBarangay(jp.getBarangay());
                job.setCity(jp.getCity());
                User poster = jp.getPoster();
                if (poster != null) {
                    job.setPoster(UserSummaryDTO.builder()
                        .id(poster.getId())
                        .fullName((poster.getFirstName() == null ? "" : poster.getFirstName()) + " " + (poster.getLastName() == null ? "" : poster.getLastName()))
                        .avatarUrl(poster.getProfileImage())
                        .barangay(poster.getAddress())
                        .city(poster.getAddress())
                        .phoneVerified(Boolean.TRUE.equals(poster.getPhoneVerified()))
                        .ratingAverage(poster.getRatingAverage() != null ? poster.getRatingAverage() : 0.0)
                        .ratingCount(poster.getRatingCount() != null ? poster.getRatingCount() : 0)
                        .completedGigs(poster.getCompletedGigs() != null ? poster.getCompletedGigs() : 0)
                        .build());
                }
            }
        } catch (Exception ignored) {}
        
        // Contact gating: only show contact info when status is SCREENING or ACCEPTED
        ApplicationStatus status = a.getStatus() != null ? a.getStatus() : ApplicationStatus.PENDING;
        ContactInfoDTO applicantContact = null;
        ContactInfoDTO posterContact = null;
        if (status == ApplicationStatus.SCREENING || status == ApplicationStatus.ACCEPTED) {
            User applicant = a.getApplicant();
            User poster = a.getJob().getPoster();
            
            applicantContact = new ContactInfoDTO();
            applicantContact.setPhoneNumber(applicant.getPhone());
            applicantContact.setViberNumber(applicant.getViberNumber());
            applicantContact.setMessengerUsername(applicant.getMessengerUsername());
            
            posterContact = new ContactInfoDTO();
            posterContact.setPhoneNumber(poster.getPhone());
            posterContact.setViberNumber(poster.getViberNumber());
            posterContact.setMessengerUsername(poster.getMessengerUsername());
        }
        
        return ApplicationDTO.builder()
            .id(a.getId())
            .jobId(a.getJob().getId())
            .job(job)
            .applicant(app)
            .pitch(a.getPitch())
            .status(status)
            .applicantContact(applicantContact)
            .posterContact(posterContact)
            .createdAt(a.getCreatedAt())
            .reportedByMe(a.getReportedByMe() != null && a.getReportedByMe())
            .build();
    }
}
