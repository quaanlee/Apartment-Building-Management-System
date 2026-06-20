package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {

    Optional<PaymentMethod> findByMethodName(String methodName);
}
