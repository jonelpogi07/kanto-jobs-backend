package com.kanto_jobs.kanto_jobs.repository;

import com.kanto_jobs.kanto_jobs.domain.Appeal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppealRepository extends JpaRepository<Appeal, Long> {
}