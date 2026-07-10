package com.quan.apartment_building_management_system.service.utility;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryUploadService {
    String uploadUtilityImage(MultipartFile file) throws IOException;
    String uploadImage(MultipartFile file, String folder) throws IOException;
}
