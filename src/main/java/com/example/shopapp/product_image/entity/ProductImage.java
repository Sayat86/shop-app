package com.example.shopapp.product_image.entity;

import com.example.shopapp.product.entity.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private Boolean isMain;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
