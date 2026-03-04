package com.example.shopapp.product.repository;

import com.example.shopapp.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByCreatedAtAsc(Long productId);

    @Modifying
    @Query("""
       update ProductImage pi
       set pi.mainImage = false
       where pi.product.id = :productId
       """)
    void clearMainImage(Long productId);

    @Query("""
       select pi.url
       from ProductImage pi
       where pi.product.id = :productId
       and pi.mainImage = true
       """)
    Optional<String> findMainImageUrl(Long productId);
}
