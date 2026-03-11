package com.example.shopapp.product.service;

import com.example.shopapp.brand.entity.Brand;
import com.example.shopapp.brand.repository.BrandRepository;
import com.example.shopapp.category.entity.Category;
import com.example.shopapp.category.repository.CategoryRepository;
import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.product.dto.*;
import com.example.shopapp.product.entity.Product;
import com.example.shopapp.product.entity.ProductImage;
import com.example.shopapp.product.mapper.ProductMapper;
import com.example.shopapp.product.repository.ProductImageRepository;
import com.example.shopapp.product.repository.ProductRepository;
import com.example.shopapp.product.specification.ProductSpecification;
import com.example.shopapp.product.variant.dto.ProductVariantResponse;
import com.example.shopapp.product.variant.entity.ProductVariant;
import com.example.shopapp.product.variant.mapper.ProductVariantMapper;
import com.example.shopapp.product.variant.repository.ProductVariantRepository;
import com.example.shopapp.product.view.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final ProductImageRepository imageRepository;
    private final BrandRepository brandRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantMapper variantMapper;
    private final ProductViewService productViewService;

    public ProductResponse create(ProductRequest request) {

        validateSlug(request.slug(), null);

        Category category = getCategoryOrThrow(request.categoryId());

        Brand brand = getBrandOrThrow(request.brandId());

        Product product = Product.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .status(request.status())
                .category(category)
                .brand(brand)
                .build();

        repository.save(product);

        return mapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(
            ProductFilter filter,
            Pageable pageable
    ) {

        Specification<Product> specification =
                ProductSpecification.withFilters(filter);

        return repository.findAll(specification, pageable)
                .map(this::mapProduct);
    }

    @CacheEvict(value = "product", key = "#request.slug")
    public ProductResponse update(Long id, ProductRequest request) {

        Product product = getProductOrThrow(id);

        validateSlug(request.slug(), product.getSlug());

        Category category = getCategoryOrThrow(request.categoryId());

        Brand brand = getBrandOrThrow(request.brandId());

        product.setName(request.name());
        product.setSlug(request.slug());
        product.setDescription(request.description());
        product.setStatus(request.status());
        product.setCategory(category);
        product.setBrand(brand);

        return mapper.toResponse(repository.save(product));
    }

    public void delete(Long id) {
        Product product = getProductOrThrow(id);

        product.setDeleted(true);
    }

    @Cacheable(cacheNames = "related-products", key = "#slug")
    @Transactional(readOnly = true)
    public List<ProductResponse> getRelatedProducts(String slug, int limit) {

        Product product = repository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Page<Product> related = repository.findRelatedProducts(
                product.getCategory().getId(),
                product.getId(),
                PageRequest.of(0, limit)
        );

        return related.stream()
                .map(this::mapProductWithImage)
                .toList();
    }

    @Cacheable(value = "product", key = "#slug")
    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {

        Product product = repository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        productViewService.incrementView(product.getId());

        return mapProduct(product);
    }

    @Cacheable(cacheNames = "popular-products")
    @Transactional(readOnly = true)
    public List<ProductResponse> getPopularProducts(int limit) {

        List<Long> ids = productViewService.getTopProducts(limit);

        if (ids.isEmpty()) {
            return List.of();
        }

        return repository.findAllById(ids)
                .stream()
                .map(this::mapProductWithImage)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductCardResponse> getProductCards(Pageable pageable) {

        return repository.findProductCards(pageable);
    }

    @Transactional(readOnly = true)
    public ProductDetailsResponse getProductDetails(String slug) {

        Product product = repository.findBySlugWithImagesAndBrand(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<ProductVariantResponse> variants = variantRepository
                .findByProductIdAndDeletedFalse(product.getId())
                .stream()
                .map(v -> new ProductVariantResponse(
                        v.getId(),
                        v.getProduct().getId(),
                        v.getSku(),
                        v.getPrice(),
                        v.getStockQuantity()
                ))
                .toList();

        List<String> images = product.getImages()
                .stream()
                .map(ProductImage::getUrl)
                .toList();

        return new ProductDetailsResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getBrand().getName(),
                product.getAverageRating(),
                product.getReviewCount(),
                images,
                variants
        );
    }

    @Cacheable(value = "catalog", key = "#pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<ProductCardResponse> getCatalog(Pageable pageable) {

        return repository.findProductCards(pageable);
    }

    private Brand getBrandOrThrow(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
    }

    private Product getProductOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private void validateSlug(String newSlug, String currentSlug) {

        if (!newSlug.equals(currentSlug) && repository.existsBySlug(newSlug)) {
            throw new BadRequestException("Slug already exists");
        }
    }

    private ProductResponse mapProductWithImage(Product product) {

        String mainImage = imageRepository
                .findMainImageUrl(product.getId())
                .orElse(null);

        List<ProductVariantResponse> variants = variantRepository
                .findByProductIdAndDeletedFalse(product.getId())
                .stream()
                .map(this::mapVariant)
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getStatus(),
                product.getCategory().getId(),
                product.getBrand().getName(),
                mainImage,
                product.getAverageRating(),
                product.getReviewCount(),
                variants,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private ProductResponse mapProduct(Product product) {

        ProductResponse base = mapper.toResponse(product);

        String mainImage = imageRepository
                .findMainImageUrl(product.getId())
                .orElse(null);

        List<ProductVariantResponse> variants =
                variantRepository.findByProductId(product.getId())
                        .stream()
                        .map(variantMapper::toResponse)
                        .toList();

        return new ProductResponse(
                base.id(),
                base.name(),
                base.slug(),
                base.description(),
                base.status(),
                base.categoryId(),
                base.brandName(),
                mainImage,
                product.getAverageRating(),
                product.getReviewCount(),
                variants,
                base.createdAt(),
                base.updatedAt()
        );
    }

    private ProductVariantResponse mapVariant(ProductVariant variant) {

        return new ProductVariantResponse(
                variant.getId(),
                variant.getProduct().getId(),
                variant.getSku(),
                variant.getPrice(),
                variant.getStockQuantity()
        );
    }
}
