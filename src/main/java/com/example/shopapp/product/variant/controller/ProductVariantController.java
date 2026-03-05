package com.example.shopapp.product.variant.controller;

import com.example.shopapp.product.variant.dto.CreateVariantRequest;
import com.example.shopapp.product.variant.dto.ProductVariantResponse;
import com.example.shopapp.product.variant.service.ProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService service;

    @PostMapping
    public ResponseEntity<ProductVariantResponse> createVariant(
            @RequestBody @Valid CreateVariantRequest request) {

        return ResponseEntity.ok(service.createVariant(request));
    }
}
