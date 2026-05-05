package com.example.demo.Service;

import com.example.demo.Entity.PaymentEntity;
import com.example.demo.Repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // Inyección por constructor (recomendado)
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Metodo simple para probar el backend
    public List<PaymentEntity> getAllPayments() {
        return paymentRepository.findAll();
    }
}
