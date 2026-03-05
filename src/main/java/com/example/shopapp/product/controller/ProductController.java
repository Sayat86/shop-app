package com.example.shopapp.product.controller;

import com.example.shopapp.product.dto.*;
import com.example.shopapp.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<ProductResponse>> getAll(
            ProductFilter filter,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(service.getAll(filter, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getBySlug(
            @PathVariable String slug
    ) {

        return ResponseEntity.ok(service.getBySlug(slug));
    }

    @GetMapping("/slug/{slug}/related")
    public ResponseEntity<List<ProductResponse>> getRelatedProducts(
            @PathVariable String slug,
            @RequestParam(defaultValue = "4") int limit
    ) {

        return ResponseEntity.ok(service.getRelatedProducts(slug, limit));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductResponse>> getPopularProducts(
            @RequestParam(defaultValue = "8") int limit
    ) {

        return ResponseEntity.ok(service.getPopularProducts(limit));
    }

    @GetMapping("/catalog")
    public ResponseEntity<Page<ProductCardResponse>> getCatalog(
            Pageable pageable
    ) {

        return ResponseEntity.ok(service.getProductCards(pageable));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProductDetailsResponse> getProductDetails(
            @PathVariable String slug) {

        return ResponseEntity.ok(service.getProductDetails(slug));
    }

    @GetMapping
    public ResponseEntity<Page<ProductCardResponse>> getProducts(
            Pageable pageable) {

        return ResponseEntity.ok(service.getCatalog(pageable));
    }
}
