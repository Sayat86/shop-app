package com.example.shopapp.product.dto;

public record ProductImageResponse(
        Long id,
        String url,
        boolean mainImage
) {}
