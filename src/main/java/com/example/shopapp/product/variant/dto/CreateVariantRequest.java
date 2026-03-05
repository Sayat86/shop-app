package com.example.shopapp.product.variant.dto;

import java.math.BigDecimal;

public record CreateVariantRequest(

        Long productId,
        String sku,
        BigDecimal price,
        Integer stockQuantity

) {}
