package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.common.CurrentUser;
import com.kanto_jobs.kanto_jobs.domain.User;
import com.kanto_jobs.kanto_jobs.dto.*;
import com.kanto_jobs.kanto_jobs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final CurrentUser currentUser;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public UserDTO me() { return toDto(currentUser.get()); }

    @PutMapping("/me")
    public UserDTO update(@RequestBody UpdateUserRequest req) {
        User u = currentUser.get();
        if (req.getFirstName() != null) u.setFirstName(req.getFirstName());
        if (req.getLastName() != null) u.setLastName(req.getLastName());
        if (req.getPhone() != null) u.setPhone(req.getPhone());
        if (req.getAddress() != null) u.setAddress(req.getAddress());
        if (req.getLat() != null) u.setLat(req.getLat());
        if (req.getLng() != null) u.setLng(req.getLng());
        if (req.getProfileImage() != null) u.setProfileImage(req.getProfileImage());
        if (req.getBio() != null) u.setBio(req.getBio());
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

    private UserDTO toDto(User u) {
        UserDTO d = new UserDTO();
        d.setId(u.getId()); d.setEmail(u.getEmail()); d.setFirstName(u.getFirstName()); d.setLastName(u.getLastName());
        d.setPhone(u.getPhone()); d.setAddress(u.getAddress()); d.setLat(u.getLat()); d.setLng(u.getLng());
        d.setProfileImage(u.getProfileImage()); d.setBio(u.getBio()); d.setGigCoins(u.getGigCoins());
        return d;
    }
}