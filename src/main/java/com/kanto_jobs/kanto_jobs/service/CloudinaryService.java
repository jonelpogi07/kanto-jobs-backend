package com.kanto_jobs.kanto_jobs.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
    private final Cloudinary cloudinary;

    private static final long MAX_SIZE = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED = Set.of("image/jpeg","image/png","image/webp","image/gif");

    public record UploadResult(String secureUrl, String publicId) {}

    public UploadResult upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File must not be empty");
        if (file.getSize() > MAX_SIZE) throw new IllegalArgumentException("File too large");
        String ct = file.getContentType();
        if (ct == null || !ALLOWED.contains(ct.toLowerCase())) throw new IllegalArgumentException("Unsupported image type");
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> res = cloudinary.uploader().upload(file.getBytes(), Map.of("folder", folder));
            return new UploadResult((String) res.get("secure_url"), (String) res.get("public_id"));
        } catch (Exception e) {
            log.error("Cloudinary upload failed", e);
            throw new RuntimeException("Upload failed");
        }
    }
}
