package com.kanto_jobs.kanto_jobs.seed;

import com.kanto_jobs.kanto_jobs.domain.User;
import com.kanto_jobs.kanto_jobs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User u = User.builder().email("jonel@kantojobs.ph")
                .password(passwordEncoder.encode("password123"))
                .firstName("Jonel").lastName("Dela Cruz").gigCoins(100).build();
            userRepository.save(u);
        }
    }
}