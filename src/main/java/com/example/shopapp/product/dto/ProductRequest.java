package com.example.shopapp.product.dto;

import com.example.shopapp.product.entity.ProductStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank
        String name,

        @NotBlank
        String slug,

        String description,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal price,

        @NotNull
        @PositiveOrZero
        Integer stockQuantity,

        @NotNull
        ProductStatus status,

        @NotNull
        Long categoryId

) {}
