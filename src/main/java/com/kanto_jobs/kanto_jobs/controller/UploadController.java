package com.kanto_jobs.kanto_jobs.controller;

import com.kanto_jobs.kanto_jobs.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {
    private final CloudinaryService cloudinaryService;

    @PostMapping("/profile")
    public java.util.Map<String,String> profile(@RequestParam("file") MultipartFile file) {
        var r = cloudinaryService.upload(file, "gig-marketplace/profiles");
        return java.util.Map.of("url", r.secureUrl());
    }
    @PostMapping("/gig")
    public java.util.Map<String,String> gig(@RequestParam("file") MultipartFile file) {
        var r = cloudinaryService.upload(file, "gig-marketplace/gigs");
        return java.util.Map.of("url", r.secureUrl());
    }
    @PostMapping("/appeal")
    public java.util.Map<String,String> appeal(@RequestParam("file") MultipartFile file) {
        var r = cloudinaryService.upload(file, "gig-marketplace/appeals");
        return java.util.Map.of("url", r.secureUrl());
    }
}
