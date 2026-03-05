package com.example.shopapp.product.variant.service;

import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.product.entity.Product;
import com.example.shopapp.product.repository.ProductRepository;
import com.example.shopapp.product.variant.dto.CreateVariantRequest;
import com.example.shopapp.product.variant.dto.ProductVariantResponse;
import com.example.shopapp.product.variant.entity.ProductVariant;
import com.example.shopapp.product.variant.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariantService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;

    public ProductVariantResponse createVariant(CreateVariantRequest request) {
        if (variantRepository.existsBySkuAndDeletedFalse(request.sku())) {
            throw new BadRequestException("SKU already exists");
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(request.sku())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .build();

        return mapToResponse(variantRepository.save(variant));
    }

    private ProductVariantResponse mapToResponse(ProductVariant variant) {

        return new ProductVariantResponse(
                variant.getId(),
                variant.getProduct().getId(),
                variant.getSku(),
                variant.getPrice(),
                variant.getStockQuantity()
        );
    }
}
