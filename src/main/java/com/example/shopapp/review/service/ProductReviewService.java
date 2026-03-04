package com.example.shopapp.review.service;

import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.product.entity.Product;
import com.example.shopapp.product.repository.ProductRepository;
import com.example.shopapp.review.dto.ProductReviewRequest;
import com.example.shopapp.review.dto.ProductReviewResponse;
import com.example.shopapp.review.entity.ProductReview;
import com.example.shopapp.review.mapper.ProductReviewMapper;
import com.example.shopapp.review.repository.ProductReviewRepository;
import com.example.shopapp.security.SecurityUtils;
import com.example.shopapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ProductReviewMapper mapper;

    public ProductReviewResponse createReview(Long productId, ProductReviewRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new BadRequestException("User already reviewed this product");
        }

        ProductReview review = ProductReview.builder()
                .rating(request.rating())
                .comment(request.comment())
                .product(product)
                .user(new User(userId)) // или найти через UserRepository
                .build();

        reviewRepository.save(review);

        updateProductRating(product);

        return mapper.toResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ProductReviewResponse> getReviews(
            Long productId,
            Pageable pageable
    ) {

        return reviewRepository
                .findByProductId(productId, pageable)
                .map(mapper::toResponse);
    }

    private void updateProductRating(Product product) {

        Double avg = reviewRepository.getAverageRating(product.getId());

        int count = reviewRepository.countByProductId(product.getId());

        product.setAverageRating(avg != null ? avg : 0.0);
        product.setReviewCount(count);
    }
}
