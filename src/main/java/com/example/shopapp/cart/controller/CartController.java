package com.example.shopapp.cart.controller;

import com.example.shopapp.cart.dto.*;
import com.example.shopapp.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(service.getCart());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request) {

        return ResponseEntity.ok(service.addToCart(request));
    }

    @PutMapping("/items/{variantId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long variantId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        return ResponseEntity.ok(
                service.updateQuantity(variantId, request)
        );
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                service.removeFromCart(productId)
        );
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart() {
        return ResponseEntity.ok(service.clearCart());
    }
}