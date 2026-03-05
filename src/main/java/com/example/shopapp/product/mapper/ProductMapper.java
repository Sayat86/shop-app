package com.example.shopapp.product.mapper;

import com.example.shopapp.product.dto.ProductResponse;
import com.example.shopapp.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "mainImageUrl", ignore = true)
    @Mapping(target = "variants", ignore = true)
    ProductResponse toResponse(Product product);
}
