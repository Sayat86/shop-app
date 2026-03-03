package com.example.shopapp.category.controller;

import com.example.shopapp.category.dto.CategoryRequest;
import com.example.shopapp.category.dto.CategoryResponse;
import com.example.shopapp.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = service.create(request);

        URI location = URI.create("/api/categories/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryResponse>> getTree() {
        return ResponseEntity.ok(service.getTree());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
