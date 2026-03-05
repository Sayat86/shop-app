package com.example.shopapp.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(

        Long id,
        Long variantId,
        String sku,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal
) {}