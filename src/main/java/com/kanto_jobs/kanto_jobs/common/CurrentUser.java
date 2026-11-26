package com.kanto_jobs.kanto_jobs.common;

import com.kanto_jobs.kanto_jobs.domain.User;
import com.kanto_jobs.kanto_jobs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUser {
    private final UserRepository userRepository;
    public User get() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        String email = a.getName();
        return userRepository.findByEmail(email).orElseThrow();
    }
}