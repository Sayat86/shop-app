package com.example.shopapp.product.catalog.dto;

import java.math.BigDecimal;

public record ProductCatalogResponse(

        Long id,
        String name,
        String slug,
        BigDecimal price,
        String imageUrl,
        Double averageRating,
        Integer reviewCount

) {}
