package com.example.shopapp.product.service;

import com.example.shopapp.category.entity.Category;
import com.example.shopapp.category.repository.CategoryRepository;
import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.product.dto.ProductFilter;
import com.example.shopapp.product.dto.ProductRequest;
import com.example.shopapp.product.dto.ProductResponse;
import com.example.shopapp.product.entity.Product;
import com.example.shopapp.product.mapper.ProductMapper;
import com.example.shopapp.product.repository.ProductRepository;
import com.example.shopapp.product.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    public ProductResponse create(ProductRequest request) {

        if (repository.existsBySlug(request.slug())) {
            throw new BadRequestException("Slug already exists");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .status(request.status())
                .category(category)
                .build();

        return mapper.toResponse(repository.save(product));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(
            ProductFilter filter,
            Pageable pageable
    ) {

        Specification<Product> specification =
                ProductSpecification.withFilters(filter);

        return repository.findAll(specification, pageable)
                .map(mapper::toResponse);
    }
}
