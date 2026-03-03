package com.example.shopapp.product.mapper;

import com.example.shopapp.product.dto.ProductResponse;
import com.example.shopapp.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    ProductResponse toResponse(Product product);
}
