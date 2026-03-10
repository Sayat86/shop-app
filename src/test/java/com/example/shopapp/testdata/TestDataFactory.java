package com.example.shopapp.testdata;

import com.example.shopapp.brand.entity.Brand;
import com.example.shopapp.category.entity.Category;
import com.example.shopapp.inventory.reservation.ReservationStatus;
import com.example.shopapp.inventory.reservation.StockReservation;
import com.example.shopapp.product.entity.Product;
import com.example.shopapp.product.entity.ProductStatus;
import com.example.shopapp.product.variant.entity.ProductVariant;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestDataFactory {

    public static Brand brand(TestEntityManager em) {
        Brand brand = new Brand();
        brand.setName("Test Brand");
        brand.setSlug("test-brand");
        return em.persist(brand);
    }

    public static Category category(TestEntityManager em) {
        Category category = new Category();
        category.setName("Test Category");
        category.setSlug("test-category");
        return em.persist(category);
    }

    public static Product product(TestEntityManager em) {

        Brand brand = brand(em);
        Category category = category(em);

        Product product = new Product();
        product.setName("Test Product");
        product.setSlug("product-" + UUID.randomUUID());
        product.setBrand(brand);
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);
        product.setAverageRating(0.0);
        product.setReviewCount(0);
        product.setViews(0L);

        return em.persist(product);
    }

    public static ProductVariant variant(TestEntityManager em) {

        Product product = product(em);

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku("TEST-SKU");
        variant.setPrice(BigDecimal.valueOf(100));
        variant.setStockQuantity(10);

        return em.persist(variant);
    }

    public static StockReservation reservation(TestEntityManager em, ProductVariant variant, int quantity) {

        StockReservation reservation = StockReservation.builder()
                .variant(variant)
                .quantity(quantity)
                .status(ReservationStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        return em.persist(reservation);
    }
}
