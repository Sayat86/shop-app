package com.example.shopapp.payment.dto;

import com.example.shopapp.payment.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(

        Long id,
        String orderNumber,
        BigDecimal amount,
        PaymentStatus status

) {}
