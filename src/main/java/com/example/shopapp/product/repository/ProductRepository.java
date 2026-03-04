package com.example.shopapp.product.repository;

import com.example.shopapp.product.dto.ProductCardResponse;
import com.example.shopapp.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByCategoryIdAndDeletedFalse(Long categoryId);

    @Query("""
       select p
       from Product p
       where p.category.id = :categoryId
       and p.id <> :productId
       and p.deleted = false
       order by p.createdAt desc
       """)
    Page<Product> findRelatedProducts(
            Long categoryId,
            Long productId,
            Pageable pageable
    );

    @Modifying
    @Query("""
       update Product p
       set p.views = p.views + 1
       where p.id = :productId
       """)
    void incrementViews(Long productId);

    Page<Product> findByDeletedFalseOrderByViewsDesc(Pageable pageable);

    @Query("""
       select new com.example.shopapp.product.dto.ProductCardResponse(
           p.id,
           p.name,
           p.slug,
           p.price,
           i.url,
           p.averageRating,
           p.reviewCount
       )
       from Product p
       left join p.images i
       where (i.mainImage = true or i.id is null)
       """)
    Page<ProductCardResponse> findProductCards(Pageable pageable);
}
