package com.example.shopapp.product.variant.dto;

import java.math.BigDecimal;

public record ProductVariantResponse(

        Long id,
        String sku,
        BigDecimal price,
        Integer stockQuantity

) {}
