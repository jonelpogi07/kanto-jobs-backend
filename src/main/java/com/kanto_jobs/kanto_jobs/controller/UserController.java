package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.common.CurrentUser;
import com.kanto_jobs.kanto_jobs.domain.User;
import com.kanto_jobs.kanto_jobs.domain.Review;
import com.kanto_jobs.kanto_jobs.dto.*;
import com.kanto_jobs.kanto_jobs.repository.UserRepository;
import com.kanto_jobs.kanto_jobs.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final CurrentUser currentUser;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @GetMapping("/me")
    public UserDTO me() { return toDto(currentUser.get()); }

    @PutMapping("/me")
    public UserDTO update(@RequestBody UpdateUserRequest req) {
        User u = currentUser.get();
        if (req.getFirstName() != null) u.setFirstName(req.getFirstName());
        if (req.getLastName() != null) u.setLastName(req.getLastName());
        if (req.getPhone() != null) u.setPhone(req.getPhone());
        if (req.getAddress() != null) u.setAddress(req.getAddress());
        if (req.getProfileImage() != null) u.setProfileImage(req.getProfileImage());
        if (req.getBio() != null) u.setBio(req.getBio());
        if (req.getSkills() != null) u.setSkills(req.getSkills());
        if (req.getAvailableDays() != null) u.setAvailableDays(req.getAvailableDays());
        if (req.getRateType() != null) u.setRateType(req.getRateType());
        if (req.getIsNegotiable() != null) u.setIsNegotiable(req.getIsNegotiable());
        userRepository.save(u);
        return toDto(u);
    }

    @PutMapping("/me/gig-coins")
    public UserDTO gigCoins(@RequestBody java.util.Map<String,Integer> body) {
        User u = currentUser.get();
        u.setGigCoins(body.getOrDefault("gigCoins", u.getGigCoins()));
        userRepository.save(u);
        return toDto(u);
    }

    @GetMapping("/{id}")
    public UserDTO byId(@PathVariable Long id) {
        return toDto(userRepository.findById(id).orElseThrow());
    }

    @GetMapping("/{id}/reviews")
    public Page<ReviewDTO> reviewsByUser(@PathVariable Long id,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return reviewRepository.findByRevieweeId(id, pageable).map(this::toReviewDto);
    }

    private ReviewDTO toReviewDto(Review r) {
        User u = r.getReviewer();
        UserSummaryDTO reviewer = UserSummaryDTO.builder().id(u.getId())
            .fullName(u.getFirstName()+" "+u.getLastName()).avatarUrl(u.getProfileImage())
            .barangay(u.getAddress()).city(u.getAddress()).phoneVerified(false).ratingAverage(0.0).ratingCount(0).completedGigs(0).build();
        
        User reviewee = userRepository.findById(r.getRevieweeId()).orElse(null);
        double ratingAverage = reviewee != null ? reviewee.getRatingAverage() : 0.0;
        int ratingCount = reviewee != null ? reviewee.getRatingCount() : 0;
        
        return ReviewDTO.builder().id(r.getId()).jobId(r.getJobId()).reviewer(reviewer)
            .revieweeId(r.getRevieweeId()).rating(r.getRating()).comment(r.getComment()).createdAt(r.getCreatedAt()).build();
    }

    private UserDTO toDto(User u) {
        UserDTO d = new UserDTO();
        d.setId(u.getId()); d.setEmail(u.getEmail()); d.setFirstName(u.getFirstName()); d.setLastName(u.getLastName());
        d.setPhone(u.getPhone()); d.setAddress(u.getAddress());
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