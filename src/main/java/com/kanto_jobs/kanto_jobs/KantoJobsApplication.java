package com.kanto_jobs.kanto_jobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KantoJobsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KantoJobsApplication.class, args);
	}

}
