package com.example.shopapp.product.dto;

import com.example.shopapp.product.entity.ProductStatus;

import java.math.BigDecimal;

public record ProductFilter(

        String name,

        Long categoryId,

        ProductStatus status,

        BigDecimal minPrice,

        BigDecimal maxPrice

) {}
