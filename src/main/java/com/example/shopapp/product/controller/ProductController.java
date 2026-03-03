package com.example.shopapp.product.controller;

import com.example.shopapp.product.dto.ProductFilter;
import com.example.shopapp.product.dto.ProductRequest;
import com.example.shopapp.product.dto.ProductResponse;
import com.example.shopapp.product.entity.ProductStatus;
import com.example.shopapp.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response = service.create(request);

        URI location = URI.create("/api/products/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public Page<ProductResponse> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable
    ) {

        ProductFilter filter = new ProductFilter(
                name,
                categoryId,
                status,
                minPrice,
                maxPrice
        );

        return service.getAll(filter, pageable);
    }
}
