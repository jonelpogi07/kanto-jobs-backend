package com.kanto_jobs.kanto_jobs.repository;

import com.kanto_jobs.kanto_jobs.domain.JobPost;
import com.kanto_jobs.kanto_jobs.domain.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    List<JobPost> findByPosterId(Long posterId);
    List<JobPost> findByStatus(JobStatus status);

    @Query("SELECT j FROM JobPost j WHERE j.status = 'OPEN'")
    List<JobPost> findOpenJobs();
}