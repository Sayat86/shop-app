package com.example.shopapp.review.mapper;

import com.example.shopapp.review.dto.ProductReviewResponse;
import com.example.shopapp.review.entity.ProductReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "userId", source = "user.id")
    ProductReviewResponse toResponse(ProductReview review);
}
