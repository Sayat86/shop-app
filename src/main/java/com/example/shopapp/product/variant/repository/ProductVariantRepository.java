package com.example.shopapp.product.variant.repository;

import com.example.shopapp.product.variant.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductId(Long productId);

    boolean existsBySkuAndDeletedFalse(String sku);

    List<ProductVariant> findByProductIdAndDeletedFalse(Long productId);
}
