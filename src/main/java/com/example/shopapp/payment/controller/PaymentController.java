package com.example.shopapp.payment.controller;

import com.example.shopapp.payment.dto.PaymentResponse;
import com.example.shopapp.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderNumber}")
    public PaymentResponse createPayment(@PathVariable String orderNumber) {
        return paymentService.createPayment(orderNumber);
    }

    @PostMapping("/{paymentId}/confirm")
    public PaymentResponse confirmPayment(@PathVariable Long paymentId) {
        return paymentService.confirmPayment(paymentId);
    }

}
