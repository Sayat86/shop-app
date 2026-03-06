package com.example.shopapp.product.variant.repository;

import com.example.shopapp.product.variant.entity.ProductVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductId(Long productId);

    boolean existsBySkuAndDeletedFalse(String sku);

    List<ProductVariant> findByProductIdAndDeletedFalse(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       select v
       from ProductVariant v
       where v.id = :variantId
       """)
    Optional<ProductVariant> findByIdForUpdate(Long variantId);
}
