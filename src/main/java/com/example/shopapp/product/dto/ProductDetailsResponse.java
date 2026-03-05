package com.example.shopapp.product.dto;

import com.example.shopapp.product.variant.dto.ProductVariantResponse;

import java.util.List;

public record ProductDetailsResponse(

        Long id,
        String name,
        String slug,
        String description,

        String brandName,

        Double averageRating,
        Integer reviewCount,

        List<String> images,
        List<ProductVariantResponse> variants

) {}
