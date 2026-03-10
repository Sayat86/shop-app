package com.example.shopapp.product.service;

import com.example.shopapp.brand.entity.Brand;
import com.example.shopapp.brand.repository.BrandRepository;
import com.example.shopapp.category.entity.Category;
import com.example.shopapp.category.repository.CategoryRepository;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.product.dto.ProductRequest;
import com.example.shopapp.product.dto.ProductResponse;
import com.example.shopapp.product.entity.Product;
import com.example.shopapp.product.entity.ProductStatus;
import com.example.shopapp.product.mapper.ProductMapper;
import com.example.shopapp.product.repository.ProductImageRepository;
import com.example.shopapp.product.repository.ProductRepository;
import com.example.shopapp.product.variant.mapper.ProductVariantMapper;
import com.example.shopapp.product.variant.repository.ProductVariantRepository;
import com.example.shopapp.testdata.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductImageRepository imageRepository;

    @Mock
    private ProductVariantRepository variantRepository;

    @Mock
    private ProductMapper mapper;

    @Mock
    private ProductVariantMapper variantMapper;

    @InjectMocks
    private ProductService service;

    private Product product;

    @BeforeEach
    void setup() {

        product = new Product();
        product.setId(1L);
        product.setName("iPhone");
        product.setSlug("iphone");
        product.setDeleted(false);
    }

    @Test
    void shouldReturnProductBySlug() {

        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone");
        product.setSlug("iphone");

        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Apple");

        Category category = new Category();
        category.setId(1L);

        product.setBrand(brand);
        product.setCategory(category);

        when(repository.findBySlug("iphone"))
                .thenReturn(Optional.of(product));

        when(imageRepository.findMainImageUrl(anyLong()))
                .thenReturn(Optional.of("image.jpg"));

        when(mapper.toResponse(product))
                .thenReturn(TestDataFactory.productResponse());

        when(variantRepository.findByProductId(anyLong()))
                .thenReturn(List.of());

        ProductResponse result = service.getBySlug("iphone");

        assertEquals("iphone", result.slug());

        verify(repository).incrementViews(1L);
    }

    @Test
    void shouldThrowIfProductNotFound() {

        when(repository.findBySlug("iphone"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getBySlug("iphone")
        );
    }

    @Test
    void shouldSoftDeleteProduct() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        service.delete(1L);

        assertTrue(product.isDeleted());
    }

    @Test
    void shouldReturnPopularProducts() {

        Category category = new Category();
        category.setId(1L);

        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Apple");

        product.setCategory(category);
        product.setBrand(brand);

        Page<Product> page = new PageImpl<>(List.of(product));

        when(repository.findByDeletedFalseOrderByViewsDesc(any()))
                .thenReturn(page);

        when(imageRepository.findMainImageUrl(anyLong()))
                .thenReturn(Optional.of("iphone.jpg"));

        when(variantRepository.findByProductIdAndDeletedFalse(anyLong()))
                .thenReturn(List.of());

        List<ProductResponse> result =
                service.getPopularProducts(5);

        assertEquals(1, result.size());
    }

    @Test
    void shouldUpdateProduct() {

        ProductRequest request = new ProductRequest(
                "iPhone 15",
                "iphone-15",
                "desc",
                ProductStatus.ACTIVE,
                1L,
                1L
        );

        Category category = new Category();
        category.setId(1L);

        Brand brand = new Brand();
        brand.setId(1L);

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(brandRepository.findById(1L))
                .thenReturn(Optional.of(brand));

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toResponse(any()))
                .thenAnswer(invocation -> {
                    Product p = invocation.getArgument(0);

                    return new ProductResponse(
                            p.getId(),
                            p.getName(),
                            p.getSlug(),
                            p.getDescription(),
                            p.getStatus(),
                            p.getCategory().getId(),
                            p.getBrand().getName(),
                            null,
                            0.0,
                            0,
                            List.of(),
                            null,
                            null
                    );
                });

        ProductResponse response =
                service.update(1L, request);

        assertEquals("iphone-15", product.getSlug());
        assertEquals("iPhone 15", product.getName());

        verify(repository).save(product);
    }
}
