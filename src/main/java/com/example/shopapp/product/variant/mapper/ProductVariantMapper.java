package com.example.shopapp.product.variant.mapper;

import com.example.shopapp.product.variant.dto.ProductVariantResponse;
import com.example.shopapp.product.variant.entity.ProductVariant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    ProductVariantResponse toResponse(ProductVariant variant);

}
