package com.kanto_jobs.kanto_jobs.repository;

import com.kanto_jobs.kanto_jobs.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRevieweeId(Long revieweeId);
    List<Review> findByJobId(Long jobId);
}