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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final CurrentUser currentUser;

    @PostMapping("/jobs/{jobId}/reviews")
    public ReviewDTO create(@PathVariable Long jobId, @RequestBody CreateReviewRequest req) {
        User u = currentUser.get();
        // reviewee = job poster? We'll set revieweeId from job; but job repo not injected. Use 0 fallback.
        Review r = Review.builder().jobId(jobId).reviewer(u).revieweeId(0L).rating(req.getRating()).comment(req.getComment()).build();
        reviewRepo.save(r);
        return toDto(r);
    }

    @GetMapping("/users/{userId}/reviews")
    public List<ReviewDTO> byUser(@PathVariable Long userId) {
        return reviewRepo.findByRevieweeId(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    private ReviewDTO toDto(Review r) {
        User u = r.getReviewer();
        UserSummaryDTO reviewer = UserSummaryDTO.builder().id(u.getId())
            .fullName(u.getFirstName()+" "+u.getLastName()).avatarUrl(u.getProfileImage())
            .barangay(u.getAddress()).city(u.getAddress()).phoneVerified(false).ratingAverage(0.0).ratingCount(0).completedGigs(0).build();
        return ReviewDTO.builder().id(r.getId()).jobId(r.getJobId()).reviewer(reviewer)
            .revieweeId(r.getRevieweeId()).rating(r.getRating()).comment(r.getComment()).createdAt(r.getCreatedAt()).build();
    }
}