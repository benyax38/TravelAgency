package com.example.demo.Controller;

import com.example.demo.Entity.PaymentEntity;
import com.example.demo.Service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // Inyección por constructor
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Endpoint para probar el backend
    @GetMapping
    public List<PaymentEntity> getAllPaymentsController() {
        return paymentService.getAllPayments();
    }
}
