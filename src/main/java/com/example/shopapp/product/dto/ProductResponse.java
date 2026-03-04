package com.example.shopapp.product.dto;

import com.example.shopapp.product.entity.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(

        Long id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        ProductStatus status,
        Long categoryId,

        String mainImageUrl,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}
