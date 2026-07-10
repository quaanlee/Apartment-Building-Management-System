package com.quan.apartment_building_management_system.service.utility.impl;

import com.cloudinary.Cloudinary;
import com.quan.apartment_building_management_system.service.utility.CloudinaryUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryUploadServiceImpl implements CloudinaryUploadService {

    private final Cloudinary cloudinary;
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    public CloudinaryUploadServiceImpl(Cloudinary cloudinary,
                                       @Value("${cloudinary.cloud-name:}") String cloudName,
                                       @Value("${cloudinary.api-key:}") String apiKey,
                                       @Value("${cloudinary.api-secret:}") String apiSecret) {
        this.cloudinary = cloudinary;
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    @Override
    public String uploadUtilityImage(MultipartFile file) throws IOException {
        return uploadImage(file, "abms/utilities");
    }

    @Override
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new IllegalStateException("Cloudinary configuration is missing.");
        }
        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                Map.of("folder", folder, "resource_type", "image")
        );
        Object secureUrl = result.get("secure_url");
        return secureUrl != null ? secureUrl.toString() : null;
    }
}
