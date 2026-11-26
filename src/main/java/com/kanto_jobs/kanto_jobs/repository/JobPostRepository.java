package com.kanto_jobs.kanto_jobs.repository;

import com.kanto_jobs.kanto_jobs.domain.JobPost;
import com.kanto_jobs.kanto_jobs.domain.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    List<JobPost> findByPosterId(Long posterId);
    List<JobPost> findByStatus(JobStatus status);
}