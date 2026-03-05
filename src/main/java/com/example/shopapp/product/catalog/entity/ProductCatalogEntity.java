package com.example.shopapp.product.catalog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_catalog")
@Getter
@Setter
public class ProductCatalogEntity {

    @Id
    private Long id;

    private String name;

    private String slug;

    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "review_count")
    private Integer reviewCount;
}
