package com.example.shopapp.product.catalog.repository;

import com.example.shopapp.product.catalog.entity.ProductCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCatalogRepository
        extends JpaRepository<ProductCatalogEntity, Long> {

}
