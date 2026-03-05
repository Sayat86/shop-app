package com.example.shopapp.product.catalog.service;

import com.example.shopapp.product.catalog.dto.ProductCatalogResponse;
import com.example.shopapp.product.catalog.entity.ProductCatalogEntity;
import com.example.shopapp.product.catalog.repository.ProductCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCatalogService {

    private final ProductCatalogRepository repository;

    public Page<ProductCatalogResponse> getCatalog(Pageable pageable) {

        return repository.findAll(pageable)
                .map(this::mapToResponse);
    }

    private ProductCatalogResponse mapToResponse(ProductCatalogEntity e) {

        return new ProductCatalogResponse(
                e.getId(),
                e.getName(),
                e.getSlug(),
                e.getPrice(),
                e.getImageUrl(),
                e.getAverageRating(),
                e.getReviewCount()
        );
    }
}
