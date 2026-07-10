package com.quan.apartment_building_management_system.service.system;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface CloudinaryService {
    String uploadFile(MultipartFile file) throws IOException;
}
