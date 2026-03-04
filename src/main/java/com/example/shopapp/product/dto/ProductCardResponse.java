package com.example.shopapp.product.dto;

import java.math.BigDecimal;

public record ProductCardResponse(

        Long id,
        String name,
        String slug,
        BigDecimal price,
        String mainImageUrl,
        Double averageRating,
        Integer reviewCount

) {}
