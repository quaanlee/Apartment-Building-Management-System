package com.quan.apartment_building_management_system.service.apartment.impl;

import com.quan.apartment_building_management_system.entity.Apartment;
import com.quan.apartment_building_management_system.repository.ApartmentRepository;
import com.quan.apartment_building_management_system.service.apartment.ApartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.quan.apartment_building_management_system.entity.ApartmentImage;
import com.quan.apartment_building_management_system.repository.ApartmentImageRepository;
import com.quan.apartment_building_management_system.service.cloudinary.CloudinaryService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final ApartmentImageRepository apartmentImageRepository;
    private final CloudinaryService cloudinaryService;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public ApartmentServiceImpl(ApartmentRepository apartmentRepository,
                                ApartmentImageRepository apartmentImageRepository,
                                CloudinaryService cloudinaryService,
                                com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.apartmentRepository = apartmentRepository;
        this.apartmentImageRepository = apartmentImageRepository;
        this.cloudinaryService = cloudinaryService;
        this.systemLogService = systemLogService;
    }

    @Override
    public List<Apartment> findAll() {
        return apartmentRepository.findAll();
    }

    @Override
    public List<Apartment> searchApartments(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        String normalizedQuery = removeAccents(query.trim().toLowerCase());
        return findAll().stream()
                .filter(a -> {
                    String aptNum = a.getApartmentNumber() != null ? removeAccents(a.getApartmentNumber().toLowerCase()) : "";
                    return aptNum.contains(normalizedQuery);
                })
                .toList();
    }

    private String removeAccents(String s) {
        if (s == null) return "";
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    @Override
    public Optional<Apartment> findById(Integer id) {
        return apartmentRepository.findById(id);
    }

    @Override
    public Optional<Apartment> findByApartmentNumber(String apartmentNumber) {
        return apartmentRepository.findByApartmentNumber(apartmentNumber);
    }

    @Override
    @Transactional
    public Apartment save(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        apartmentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void saveApartmentImages(Apartment apartment, MultipartFile mainImage, MultipartFile subImage1, MultipartFile subImage2,
                                     boolean deleteMainImage, boolean deleteSubImage1, boolean deleteSubImage2) throws IOException {
        handleImageUploadAndDeletion(apartment, mainImage, "Main Image", true, deleteMainImage);
        handleImageUploadAndDeletion(apartment, subImage1, "Sub Image 1", false, deleteSubImage1);
        handleImageUploadAndDeletion(apartment, subImage2, "Sub Image 2", false, deleteSubImage2);
    }

    private void handleImageUploadAndDeletion(Apartment apartment, MultipartFile file, String title, boolean isPrimary, boolean deleteRequested) throws IOException {
        if (deleteRequested) {
            Optional<ApartmentImage> existingImageOpt = apartmentImageRepository
                    .findByApartmentApartmentIdAndImageTitle(apartment.getApartmentId(), title);
            if (existingImageOpt.isPresent()) {
                apartmentImageRepository.delete(existingImageOpt.get());
            }
        }

        if (file != null && !file.isEmpty()) {
            // Upload to Cloudinary
            String imageUrl = cloudinaryService.uploadFile(file);
            if (imageUrl != null) {
                Optional<ApartmentImage> existingImageOpt = apartmentImageRepository
                        .findByApartmentApartmentIdAndImageTitle(apartment.getApartmentId(), title);

                ApartmentImage image;
                if (existingImageOpt.isPresent()) {
                    image = existingImageOpt.get();
                } else {
                    image = new ApartmentImage();
                    image.setApartment(apartment);
                    image.setImageTitle(title);
                    image.setPrimary(isPrimary);
                }

                image.setImageUrl(imageUrl);
                image.setUploadedAt(LocalDateTime.now());
                apartmentImageRepository.save(image);
            }
        }
    }
}
