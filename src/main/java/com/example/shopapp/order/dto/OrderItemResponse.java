package com.example.shopapp.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(

        Long variantId,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal

) {}