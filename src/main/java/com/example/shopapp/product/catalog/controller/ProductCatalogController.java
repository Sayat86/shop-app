package com.example.shopapp.product.catalog.controller;

import com.example.shopapp.product.catalog.dto.ProductCatalogResponse;
import com.example.shopapp.product.catalog.service.ProductCatalogService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class ProductCatalogController {

    private final ProductCatalogService catalogService;

    @GetMapping
    public Page<ProductCatalogResponse> getCatalog(@ParameterObject Pageable pageable) {
        return catalogService.getCatalog(pageable);
    }
}
