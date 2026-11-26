package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.domain.User;
import com.kanto_jobs.kanto_jobs.dto.*;
import com.kanto_jobs.kanto_jobs.repository.UserRepository;
import com.kanto_jobs.kanto_jobs.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        User u = userRepository.findByEmail(req.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
        return build(u);
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) throw new ResponseStatusException(HttpStatus.CONFLICT, "email exists");
        User u = User.builder().email(req.getEmail()).password(passwordEncoder.encode(req.getPassword()))
            .firstName(req.getFirstName()).lastName(req.getLastName()).build();
        userRepository.save(u);
        return build(u);
    }

    private AuthResponse build(User u) {
        return AuthResponse.builder().accessToken(jwtService.issue(u)).tokenType("Bearer")
            .expiresIn(jwtService.getExpiryMs()).user(toDto(u)).build();
    }

    private UserDTO toDto(User u) {
        UserDTO d = new UserDTO();
        d.setId(u.getId()); d.setEmail(u.getEmail()); d.setFirstName(u.getFirstName()); d.setLastName(u.getLastName());
        d.setPhone(u.getPhone()); d.setAddress(u.getAddress()); d.setLat(u.getLat()); d.setLng(u.getLng());
        d.setProfileImage(u.getProfileImage()); d.setBio(u.getBio()); d.setGigCoins(u.getGigCoins());
        d.setCreatedAt(u.getCreatedAt());
        if (u.getContact() != null) {
            ContactInfoDTO c = new ContactInfoDTO();
            c.setPhoneNumber(u.getContact().getPhoneNumber());
            c.setViberNumber(u.getContact().getViberNumber());
            c.setMessengerUsername(u.getContact().getMessengerUsername());
            d.setContact(c);
        }
        d.setViberNumber(u.getViberNumber());
        d.setMessengerUsername(u.getMessengerUsername());
        d.setMerchantId(u.getMerchantId());
        d.setMerchantName(u.getMerchantName());
        d.setIsBusinessMerchant(u.getIsBusinessMerchant());
        d.setMerchantBusinessName(u.getMerchantBusinessName());
        d.setUnlimitedGigsterScreening(u.getUnlimitedGigsterScreening());
        d.setMerchantPartnershipActive(u.getMerchantPartnershipActive());
        d.setRateType(u.getRateType());
        d.setSkills(u.getSkills());
        d.setAvailableDays(u.getAvailableDays());
        d.setCompletedGigs(u.getCompletedGigs());
        d.setIsNegotiable(u.getIsNegotiable());
        d.setPhoneVerified(u.getPhoneVerified());
        return d;
    }
}