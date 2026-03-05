package com.example.shopapp.product.dto;

import com.example.shopapp.product.entity.ProductStatus;
import com.example.shopapp.product.variant.dto.ProductVariantResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(

        Long id,
        String name,
        String slug,
        String description,

        ProductStatus status,

        Long categoryId,
        String brandName,

        String mainImageUrl,

        Double averageRating,
        Integer reviewCount,

        List<ProductVariantResponse> variants,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}
