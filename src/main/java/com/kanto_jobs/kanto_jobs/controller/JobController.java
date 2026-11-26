package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.common.CurrentUser;
import com.kanto_jobs.kanto_jobs.domain.*;
import com.kanto_jobs.kanto_jobs.dto.*;
import com.kanto_jobs.kanto_jobs.repository.JobPostRepository;
import com.kanto_jobs.kanto_jobs.repository.ApplicationRepository;
import com.kanto_jobs.kanto_jobs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobPostRepository jobRepo;
    private final UserRepository userRepo;
    private final ApplicationRepository applicationRepo;
    private final CurrentUser currentUser;

    @GetMapping
    public Page<JobPostDTO> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) JobCategory category,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<JobPost> all = jobRepo.findAll();
        List<JobPostDTO> dtos = all.stream().map(j -> toDto(j, lat, lng)).collect(Collectors.toList());
        Pageable p = PageRequest.of(page, size);
        int start = (int) p.getOffset();
        int end = Math.min(start + size, dtos.size());
        List<JobPostDTO> slice = start >= dtos.size() ? List.of() : dtos.subList(start, end);
        return new PageImpl<>(slice, p, dtos.size());
    }

    @GetMapping("/{id}")
    public JobPostDTO get(@PathVariable Long id, @RequestParam(required = false) Double lat, @RequestParam(required = false) Double lng) {
        return toDto(jobRepo.findById(id).orElseThrow(), lat, lng);
    }

    @PostMapping
    public JobPostDTO create(@RequestBody CreateJobPostRequest req) {
        User u = currentUser.get();
        JobPost j = JobPost.builder()
            .title(req.getTitle()).description(req.getDescription()).category(req.getCategory())
            .compensationType(req.getCompensationType()).amount(req.getAmount()).paymentMethods(req.getPaymentMethods())
            .barangay(req.getBarangay()).city(req.getCity()).landmark(req.getLandmark())
            .latitude(req.getLatitude()).longitude(req.getLongitude()).timing(req.getTiming())
            .scheduledAt(req.getScheduledAt()).estimatedDuration(req.getEstimatedDuration())
            .images(req.getImages()).isNegotiable(req.getIsNegotiable())
            .partnerMerchantId(req.getPartnerMerchantId()).partnerMerchantName(req.getPartnerMerchantName())
            .isMerchantGig(req.getIsMerchantGig()).isCustomerOrderDelivery(req.getIsCustomerOrderDelivery())
            .merchantId(req.getMerchantId()).merchantName(req.getMerchantName()).merchantBoosted(req.getMerchantBoosted())
            .poster(u).status(JobStatus.OPEN).build();
        jobRepo.save(j);
        return toDto(j, null, null);
    }

    @PutMapping("/{id}")
    public JobPostDTO update(@PathVariable Long id, @RequestBody CreateJobPostRequest req) {
        JobPost j = jobRepo.findById(id).orElseThrow();
        j.setTitle(req.getTitle()); j.setDescription(req.getDescription()); j.setCategory(req.getCategory());
        j.setCompensationType(req.getCompensationType()); j.setAmount(req.getAmount()); j.setPaymentMethods(req.getPaymentMethods());
        j.setBarangay(req.getBarangay()); j.setCity(req.getCity()); j.setLandmark(req.getLandmark());
        j.setLatitude(req.getLatitude()); j.setLongitude(req.getLongitude()); j.setTiming(req.getTiming());
        j.setScheduledAt(req.getScheduledAt()); j.setEstimatedDuration(req.getEstimatedDuration());
        j.setImages(req.getImages()); j.setIsNegotiable(req.getIsNegotiable());
        j.setUpdatedAt(java.time.LocalDateTime.now());
        jobRepo.save(j);
        return toDto(j, null, null);
    }

    @GetMapping("/mine")
    public List<JobPostDTO> mine() {
        Long uid = currentUser.get().getId();
        return jobRepo.findByPosterId(uid).stream().map(j -> toDto(j, null, null)).collect(Collectors.toList());
    }

    @PostMapping("/{id}/complete")
    public JobPostDTO complete(@PathVariable Long id) {
        JobPost j = jobRepo.findById(id).orElseThrow();
        j.setStatus(JobStatus.COMPLETED); j.setUpdatedAt(java.time.LocalDateTime.now());
        jobRepo.save(j);
        return toDto(j, null, null);
    }

    @PostMapping("/{id}/cancel")
    public JobPostDTO cancel(@PathVariable Long id) {
        JobPost j = jobRepo.findById(id).orElseThrow();
        j.setStatus(JobStatus.CANCELLED); j.setUpdatedAt(java.time.LocalDateTime.now());
        jobRepo.save(j);
        return toDto(j, null, null);
    }

    @PostMapping("/{id}/apply")
    public ApplicationDTO apply(@PathVariable Long id, @RequestBody(required = false) java.util.Map<String,String> body) {
        JobPost j = jobRepo.findById(id).orElseThrow();
        User u = currentUser.get();
        Application a = Application.builder().job(j).applicant(u).pitch(body != null ? body.getOrDefault("pitch","") : "").build();
        applicationRepo.save(a);
        j.setApplicantCount((j.getApplicantCount()==null?0:j.getApplicantCount())+1);
        jobRepo.save(j);
        return ApplicationController.toDtoStatic(a);
    }
    private JobPostDTO toDto(JobPost j, Double lat, Double lng) {
        Double dist = null;
        if (lat != null && lng != null && j.getLatitude() != null && j.getLongitude() != null) {
            double R = 6371;
            double dLat = Math.toRadians(j.getLatitude() - lat);
            double dLng = Math.toRadians(j.getLongitude() - lng);
            double a = Math.sin(dLat/2)*Math.sin(dLat/2) + Math.cos(Math.toRadians(lat))*Math.cos(Math.toRadians(j.getLatitude()))*Math.sin(dLng/2)*Math.sin(dLng/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            dist = R * c;
        }
        UserSummaryDTO poster = UserSummaryDTO.builder().id(j.getPoster().getId())
            .fullName(j.getPoster().getFirstName() + " " + j.getPoster().getLastName())
            .avatarUrl(j.getPoster().getProfileImage()).barangay(j.getPoster().getAddress()).city(j.getPoster().getAddress())
            .phoneVerified(false).ratingAverage(0.0).ratingCount(0).completedGigs(0).build();
        return JobPostDTO.builder().id(j.getId()).title(j.getTitle()).description(j.getDescription())
            .category(j.getCategory()).compensationType(j.getCompensationType()).amount(j.getAmount())
            .paymentMethods(j.getPaymentMethods()).barangay(j.getBarangay()).city(j.getCity()).landmark(j.getLandmark())
            .latitude(j.getLatitude()).longitude(j.getLongitude()).distanceKm(dist).timing(j.getTiming())
            .scheduledAt(j.getScheduledAt()).estimatedDuration(j.getEstimatedDuration()).status(j.getStatus())
            .poster(poster).applicantCount(j.getApplicantCount()).acceptedApplicationId(j.getAcceptedApplicationId())
            .images(j.getImages()).createdAt(j.getCreatedAt()).updatedAt(j.getUpdatedAt())
            .isNegotiable(j.getIsNegotiable()).partnerMerchantId(j.getPartnerMerchantId())
            .partnerMerchantName(j.getPartnerMerchantName()).isMerchantGig(j.getIsMerchantGig())
            .isCustomerOrderDelivery(j.getIsCustomerOrderDelivery()).merchantId(j.getMerchantId())
            .merchantName(j.getMerchantName()).merchantBoosted(j.getMerchantBoosted()).reportedByMe(false).build();
    }
}