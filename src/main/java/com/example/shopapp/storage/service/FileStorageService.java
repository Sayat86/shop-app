package com.example.shopapp.storage.service;

import com.example.shopapp.config.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final UploadProperties properties;

    public String storeProductImage(MultipartFile file) {

        try {

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get(properties.getProductImages());

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/products/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file");
        }
    }
}
