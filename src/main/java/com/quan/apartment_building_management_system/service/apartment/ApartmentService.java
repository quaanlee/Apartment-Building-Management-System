package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.entity.Apartment;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ApartmentService {

    List<Apartment> findAll();

    List<Apartment> searchApartments(String query);

    Optional<Apartment> findById(Integer id);

    Optional<Apartment> findByApartmentNumber(String apartmentNumber);

    Apartment save(Apartment apartment);

    void deleteById(Integer id);

    void saveApartmentImages(Apartment apartment, MultipartFile mainImage, MultipartFile subImage1, MultipartFile subImage2,
                             boolean deleteMainImage, boolean deleteSubImage1, boolean deleteSubImage2) throws IOException;
}
