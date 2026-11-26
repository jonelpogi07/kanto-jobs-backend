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
    private final JobPostRepository jobRepo;
    private final CurrentUser currentUser;

    @PostMapping("/jobs/{jobId}/reviews")
    public ReviewDTO create(@PathVariable Long jobId, @RequestBody CreateReviewRequest req) {
        User u = currentUser.get();
        JobPost job = jobRepo.findById(jobId).orElseThrow();
        User reviewee = job.getPoster();
        Review r = Review.builder().jobId(jobId).reviewer(u).revieweeId(reviewee.getId()).rating(req.getRating()).comment(req.getComment()).build();
        reviewRepo.save(r);
        // Update reviewee's rating aggregation
        updateRatingAggregation(reviewee.getId());
        return toDto(r);
    }

    private void updateRatingAggregation(Long userId) {
        List<Review> reviews = reviewRepo.findByRevieweeId(userId);
        if (!reviews.isEmpty()) {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            int count = reviews.size();
            User u = userRepo.findById(userId).orElse(null);
            if (u != null) {
                u.setRatingAverage(avg);
                u.setRatingCount(count);
                userRepo.save(u);
            }
        }
    }

    private ReviewDTO toDto(Review r) {
        User u = r.getReviewer();
        UserSummaryDTO reviewer = UserSummaryDTO.builder().id(u.getId())
            .fullName(u.getFirstName()+" "+u.getLastName()).avatarUrl(u.getProfileImage())
            .barangay(u.getAddress()).city(u.getAddress()).phoneVerified(false).ratingAverage(0.0).ratingCount(0).completedGigs(0).build();
        
        // Calculate rating aggregation for reviewee
        User reviewee = userRepo.findById(r.getRevieweeId()).orElse(null);
        double ratingAverage = reviewee != null ? reviewee.getRatingAverage() : 0.0;
        int ratingCount = reviewee != null ? reviewee.getRatingCount() : 0;
        
        return ReviewDTO.builder().id(r.getId()).jobId(r.getJobId()).reviewer(reviewer)
            .revieweeId(r.getRevieweeId()).rating(r.getRating()).comment(r.getComment()).createdAt(r.getCreatedAt()).build();
    }
}