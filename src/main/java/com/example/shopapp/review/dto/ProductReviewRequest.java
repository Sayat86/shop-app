package com.example.shopapp.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ProductReviewRequest(

        @Min(1)
        @Max(5)
        Integer rating,
        String comment

) {}
