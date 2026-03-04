package com.example.shopapp.product.mapper;

import com.example.shopapp.product.dto.ProductImageResponse;
import com.example.shopapp.product.entity.ProductImage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    ProductImageResponse toResponse(ProductImage image);

    List<ProductImageResponse> toResponseList(List<ProductImage> images);
}
