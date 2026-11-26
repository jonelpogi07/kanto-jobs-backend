package com.kanto_jobs.kanto_jobs.common;

import com.kanto_jobs.kanto_jobs.domain.User;
import com.kanto_jobs.kanto_jobs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class CurrentUser {
    private final UserRepository userRepository;

    /** The authenticated user, or null when the request is anonymous. */
    public User getOrNull() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getName() == null) return null;
        return userRepository.findByEmail(a.getName()).orElse(null);
    }

    public User get() {
        User u = getOrNull();
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return u;
    }
}