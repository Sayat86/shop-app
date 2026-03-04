package com.example.shopapp.review.repository;

import com.example.shopapp.review.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    @Query("""
            select avg(r.rating)
            from ProductReview r
            where r.product.id = :productId
            """)
    Double getAverageRating(Long productId);

    int countByProductId(Long productId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    Page<ProductReview> findByProductId(Long productId, Pageable pageable);
}
