package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.common.CurrentUser;
import com.kanto_jobs.kanto_jobs.domain.*;
import com.kanto_jobs.kanto_jobs.dto.*;
import com.kanto_jobs.kanto_jobs.repository.JobPostRepository;
import com.kanto_jobs.kanto_jobs.repository.ApplicationRepository;
import com.kanto_jobs.kanto_jobs.repository.UserRepository;
import com.kanto_jobs.kanto_jobs.repository.ReportRepository;
import com.kanto_jobs.kanto_jobs.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobPostRepository jobRepo;
    private final UserRepository userRepo;
    private final ApplicationRepository applicationRepo;
    private final ReportRepository reportRepo;
    private final ReviewRepository reviewRepo;
    private final CurrentUser currentUser;
    private final com.kanto_jobs.kanto_jobs.service.ChatNotificationService chatNotificationService;

    @GetMapping
    public Page<JobPostDTO> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) JobCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<JobPost> all = jobRepo.findOpenJobs();
        List<JobPostDTO> dtos = all.stream().map(j -> toDto(j)).collect(Collectors.toList());
        
        // Ordering: merchant-boost → urgent → newest
        dtos.sort(Comparator
            .comparing((JobPostDTO j) -> j.getMerchantBoosted() != null && j.getMerchantBoosted() ? 0 : 1)
            .thenComparing(j -> j.getTiming() == JobTiming.URGENT ? 0 : 1)
            .thenComparing(j -> j.getCreatedAt(), Comparator.nullsLast(Comparator.reverseOrder()))
        );

        Pageable p = PageRequest.of(page, size);
        int start = (int) p.getOffset();
        int end = Math.min(start + size, dtos.size());
        List<JobPostDTO> slice = start >= dtos.size() ? List.of() : dtos.subList(start, end);
        return new PageImpl<>(slice, p, dtos.size());
    }

    @GetMapping("/{id}")
    public JobPostDTO get(@PathVariable Long id) {
        return toDto(jobRepo.findById(id).orElseThrow());
    }

    @PostMapping
    public JobPostDTO create(@RequestBody CreateJobPostRequest req) {
        User u = currentUser.get();
        JobPost j = JobPost.builder()
            .title(req.getTitle()).description(req.getDescription()).category(req.getCategory())
            .compensationType(req.getCompensationType()).amount(req.getAmount()).paymentMethods(req.getPaymentMethods())
            .barangay(req.getBarangay()).city(req.getCity()).landmark(req.getLandmark())
            .timing(req.getTiming())
            .scheduledAt(req.getScheduledAt()).estimatedDuration(req.getEstimatedDuration())
            .images(req.getImages()).isNegotiable(req.getIsNegotiable())
            .partnerMerchantId(req.getPartnerMerchantId()).partnerMerchantName(req.getPartnerMerchantName())
            .isMerchantGig(req.getIsMerchantGig()).isCustomerOrderDelivery(req.getIsCustomerOrderDelivery())
            .merchantId(req.getMerchantId()).merchantName(req.getMerchantName()).merchantBoosted(req.getMerchantBoosted())
            .poster(u).status(JobStatus.OPEN).build();
        jobRepo.save(j);
        return toDto(j);
    }

    @PutMapping("/{id}")
    public JobPostDTO update(@PathVariable Long id, @RequestBody CreateJobPostRequest req) {
        JobPost j = jobRepo.findById(id).orElseThrow();
        j.setTitle(req.getTitle()); j.setDescription(req.getDescription()); j.setCategory(req.getCategory());
        j.setCompensationType(req.getCompensationType()); j.setAmount(req.getAmount()); j.setPaymentMethods(req.getPaymentMethods());
        j.setBarangay(req.getBarangay()); j.setCity(req.getCity()); j.setLandmark(req.getLandmark());
        j.setTiming(req.getTiming());
        j.setScheduledAt(req.getScheduledAt()); j.setEstimatedDuration(req.getEstimatedDuration());
        j.setImages(req.getImages()); j.setIsNegotiable(req.getIsNegotiable());
        j.setUpdatedAt(LocalDateTime.now());
        jobRepo.save(j);
        return toDto(j);
    }

    @GetMapping("/mine")
    public List<JobPostDTO> mine() {
        Long uid = currentUser.get().getId();
        return jobRepo.findByPosterId(uid).stream().map(j -> toDto(j)).collect(Collectors.toList());
    }

    @PostMapping("/{id}/complete")
    public JobPostDTO complete(@PathVariable Long id) {
        JobPost j = jobRepo.findById(id).orElseThrow();
        j.setStatus(JobStatus.COMPLETED); j.setUpdatedAt(LocalDateTime.now());
        jobRepo.save(j);
        return toDto(j);
    }

    @PostMapping("/{id}/cancel")
    public JobPostDTO cancel(@PathVariable Long id) {
        JobPost j = jobRepo.findById(id).orElseThrow();
        j.setStatus(JobStatus.CANCELLED); j.setUpdatedAt(LocalDateTime.now());
        jobRepo.save(j);
        return toDto(j);
    }

    @PostMapping("/{id}/apply")
    public ApplicationDTO apply(@PathVariable Long id, @RequestBody(required = false) java.util.Map<String,String> body) {
        JobPost j = jobRepo.findById(id).orElseThrow();
        User u = currentUser.get();
        Application a = Application.builder().job(j).applicant(u).pitch(body != null ? body.getOrDefault("pitch","") : "").build();
        applicationRepo.save(a);
        j.setApplicantCount((j.getApplicantCount()==null?0:j.getApplicantCount())+1);
        jobRepo.save(j);
        return ApplicationController.toDtoStatic(a, userRepo, reviewRepo);
    }

    @PostMapping("/{id}/report-no-show")
    public JobPostDTO reportNoShow(@PathVariable Long id) {
        JobPost j = jobRepo.findById(id).orElseThrow();
        User u = currentUser.get();
        // Check if user is the poster
        if (!j.getPoster().getId().equals(u.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Only the poster can report no-show");
        }
        // Create report
        Report r = Report.builder()
            .reporter(u)
            .targetUser(j.getAcceptedApplicationId() != null 
                ? applicationRepo.findById(j.getAcceptedApplicationId()).map(Application::getApplicant).orElse(null)
                : null)
            .job(j)
            .application(j.getAcceptedApplicationId() != null 
                ? applicationRepo.findById(j.getAcceptedApplicationId()).orElse(null)
                : null)
            .reason("No-show reported by client")
            .status(ReportStatus.PENDING)
            .reportedAt(LocalDateTime.now())
            .appealDeadline(LocalDateTime.now().plusHours(24))
            .build();
        reportRepo.save(r);
        // Update job with report info
        j.setReportId(r.getId());
        j.setReportedAt(LocalDateTime.now());
        j.setAppealDeadline(LocalDateTime.now().plusHours(24));
        jobRepo.save(j);
        chatNotificationService.postReportNotice(r, "Client");
        return toDto(j);
    }

    @PostMapping("/search-nearby-gigsters")
    public SearchNearbyGigstersResponse searchNearbyGigsters(@RequestBody SearchNearbyGigstersRequest req) {
        // For now, return empty list - will be implemented with actual user location queries
        return new SearchNearbyGigstersResponse(List.of(), 0);
    }

    private JobPostDTO toDto(JobPost j) {
        UserSummaryDTO poster = UserSummaryDTO.builder().id(j.getPoster().getId())
            .fullName(j.getPoster().getFirstName() + " " + j.getPoster().getLastName())
            .avatarUrl(j.getPoster().getProfileImage()).barangay(j.getPoster().getAddress()).city(j.getPoster().getAddress())
            .phoneVerified(false).ratingAverage(0.0).ratingCount(0).completedGigs(0).build();
        
        // Check if current user has reported this job
        boolean reportedByMe = false;
        User current = currentUser.getOrNull();
        if (j.getReportId() != null && current != null) {
            // Check if current user is the reporter
            reportedByMe = reportRepo.existsByIdAndReporterId(j.getReportId(), current.getId());
        }
        
        return JobPostDTO.builder().id(j.getId()).title(j.getTitle()).description(j.getDescription())
            .category(j.getCategory()).compensationType(j.getCompensationType()).amount(j.getAmount())
            .paymentMethods(j.getPaymentMethods()).barangay(j.getBarangay()).city(j.getCity()).landmark(j.getLandmark())
            .timing(j.getTiming())
            .scheduledAt(j.getScheduledAt()).estimatedDuration(j.getEstimatedDuration()).status(j.getStatus())
            .poster(poster).applicantCount(j.getApplicantCount()).acceptedApplicationId(j.getAcceptedApplicationId())
            .images(j.getImages()).createdAt(j.getCreatedAt()).updatedAt(j.getUpdatedAt())
            .isNegotiable(j.getIsNegotiable()).partnerMerchantId(j.getPartnerMerchantId())
            .partnerMerchantName(j.getPartnerMerchantName()).isMerchantGig(j.getIsMerchantGig())
            .isCustomerOrderDelivery(j.getIsCustomerOrderDelivery()).merchantId(j.getMerchantId())
            .merchantName(j.getMerchantName()).merchantBoosted(j.getMerchantBoosted()).reportedByMe(reportedByMe).build();
    }
}