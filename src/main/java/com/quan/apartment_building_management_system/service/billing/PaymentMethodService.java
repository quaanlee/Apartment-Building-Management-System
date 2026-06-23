package com.quan.apartment_building_management_system.service.billing;

import com.quan.apartment_building_management_system.entity.PaymentMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PaymentMethodService {

    List<PaymentMethod> findAll();

    Optional<PaymentMethod> findById(Integer id);

    Optional<PaymentMethod> findByMethodName(String methodName);

    PaymentMethod save(PaymentMethod paymentMethod);

    void deleteById(Integer id);
}
