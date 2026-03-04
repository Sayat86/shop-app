package com.example.shopapp.product.controller;

import com.example.shopapp.product.dto.ProductImageResponse;
import com.example.shopapp.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageService service;

    @PostMapping("/api/admin/products/{productId}/images")
    public ResponseEntity<ProductImageResponse> addImage(
            @PathVariable Long productId,
            @RequestParam String url,
            @RequestParam(defaultValue = "false") boolean mainImage
    ) {
        return ResponseEntity.ok(service.addImage(productId, url, mainImage));
    }

    @GetMapping("/api/products/{productId}/images")
    public ResponseEntity<List<ProductImageResponse>> getImages(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(service.getProductImages(productId));
    }

    @DeleteMapping("/api/admin/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {

        service.deleteImage(imageId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/api/admin/products/{productId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductImageResponse> uploadImage(
            @PathVariable Long productId,
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "false") boolean mainImage
    ) {

        return ResponseEntity.ok(
                service.uploadImage(productId, file, mainImage)
        );
    }
}
