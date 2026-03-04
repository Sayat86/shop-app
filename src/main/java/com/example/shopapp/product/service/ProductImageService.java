package com.example.shopapp.product.service;

import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.product.dto.ProductImageResponse;
import com.example.shopapp.product.entity.Product;
import com.example.shopapp.product.entity.ProductImage;
import com.example.shopapp.product.mapper.ProductImageMapper;
import com.example.shopapp.product.repository.ProductImageRepository;
import com.example.shopapp.product.repository.ProductRepository;
import com.example.shopapp.storage.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageService {
    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final ProductImageMapper mapper;
    private final FileStorageService storageService;

    public ProductImageResponse addImage(Long productId, String url, boolean mainImage) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ProductImage image = ProductImage.builder()
                .product(product)
                .url(url)
                .mainImage(mainImage)
                .build();

        return mapper.toResponse(imageRepository.save(image));
    }

    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(Long productId) {

        return mapper.toResponseList(
                imageRepository.findByProductIdOrderByCreatedAtAsc(productId)
        );
    }

    public void deleteImage(Long imageId) {

        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        imageRepository.delete(image);
    }

    public ProductImageResponse uploadImage(
            Long productId,
            MultipartFile file,
            boolean mainImage
    ) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        String url = storageService.storeProductImage(file);

        ProductImage image = ProductImage.builder()
                .product(product)
                .url(url)
                .mainImage(mainImage)
                .build();

        return mapper.toResponse(imageRepository.save(image));
    }
}
