package com.example.shopapp.review.controller;

import com.example.shopapp.review.dto.ProductReviewRequest;
import com.example.shopapp.review.dto.ProductReviewResponse;
import com.example.shopapp.review.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService service;

    @PostMapping
    public ResponseEntity<ProductReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ProductReviewRequest request
    ) {

        ProductReviewResponse response = service.createReview(productId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public Page<ProductReviewResponse> getReviews(
            @PathVariable Long productId,
            Pageable pageable
    ) {

        return service.getReviews(productId, pageable);
    }
}
