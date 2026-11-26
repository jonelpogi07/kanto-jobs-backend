package com.kanto_jobs.kanto_jobs.repository;

import com.kanto_jobs.kanto_jobs.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);
    List<Application> findByApplicantId(Long applicantId);
}