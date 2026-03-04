package com.example.shopapp.review.dto;

import java.time.LocalDateTime;

public record ProductReviewResponse(

        Long id,
        Long productId,
        Long userId,
        Integer rating,
        String comment,
        LocalDateTime createdAt

) {}
