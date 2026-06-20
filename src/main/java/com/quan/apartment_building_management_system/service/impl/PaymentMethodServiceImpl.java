package com.quan.apartment_building_management_system.service.impl;

import com.quan.apartment_building_management_system.entity.PaymentMethod;
import com.quan.apartment_building_management_system.repository.PaymentMethodRepository;
import com.quan.apartment_building_management_system.service.PaymentMethodService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public List<PaymentMethod> findAll() {
        return paymentMethodRepository.findAll();
    }

    @Override
    public Optional<PaymentMethod> findById(Integer id) {
        return paymentMethodRepository.findById(id);
    }

    @Override
    public Optional<PaymentMethod> findByMethodName(String methodName) {
        return paymentMethodRepository.findByMethodName(methodName);
    }

    @Override
    @Transactional
    public PaymentMethod save(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        paymentMethodRepository.deleteById(id);
    }
}
