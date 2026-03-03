package com.example.shopapp.cart.service;

import com.example.shopapp.cart.dto.AddToCartRequest;
import com.example.shopapp.cart.dto.CartItemResponse;
import com.example.shopapp.cart.dto.CartResponse;
import com.example.shopapp.cart.dto.UpdateCartItemRequest;
import com.example.shopapp.cart.entity.Cart;
import com.example.shopapp.cart.entity.CartItem;
import com.example.shopapp.cart.repository.CartRepository;
import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.product.entity.Product;
import com.example.shopapp.product.repository.ProductRepository;
import com.example.shopapp.security.SecurityUtils;
import com.example.shopapp.user.entity.User;
import com.example.shopapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // ================= CORE METHOD =================

    public Cart getOrCreateCart() {

        Long userId = SecurityUtils.getCurrentUserId();

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    public CartResponse addToCart(AddToCartRequest request) {

        Cart cart = getOrCreateCart();

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() < request.quantity()) {
            throw new BadRequestException("Not enough stock available");
        }

        // Проверяем, есть ли уже товар в корзине
        Optional<CartItem> existingItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst();

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.quantity();

            if (product.getStockQuantity() < newQuantity) {
                throw new BadRequestException("Not enough stock available");
            }

            item.setQuantity(newQuantity);

        } else {

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .build();

            cart.getItems().add(newItem);
        }

        return mapToResponse(cart);
    }

    public CartResponse updateQuantity(Long productId, UpdateCartItemRequest request) {

        Cart cart = getOrCreateCart();

        CartItem item = cart.getItems()
                .stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        Product product = item.getProduct();

        if (product.getStockQuantity() < request.quantity()) {
            throw new BadRequestException("Not enough stock available");
        }

        item.setQuantity(request.quantity());

        return mapToResponse(cart);
    }

    public CartResponse removeFromCart(Long productId) {

        Cart cart = getOrCreateCart();

        CartItem item = cart.getItems()
                .stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        cart.getItems().remove(item);

        return mapToResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart() {

        Long userId = SecurityUtils.getCurrentUserId();

        return cartRepository.findByUserId(userId)
                .map(this::mapToResponse)
                .orElseGet(() -> new CartResponse(
                        List.of(),
                        BigDecimal.ZERO
                ));
    }

    public CartResponse clearCart() {

        Cart cart = getOrCreateCart();

        cart.getItems().clear();

        return mapToResponse(cart);
    }

    // ================= PRIVATE =================

    private Cart createCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = Cart.builder()
                .user(user)
                .build();

        return cartRepository.save(cart);
    }

    private CartResponse mapToResponse(Cart cart) {

        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(item -> {

                    BigDecimal price = item.getProduct().getPrice();
                    BigDecimal subtotal = price.multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    );

                    return new CartItemResponse(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            price,
                            item.getQuantity(),
                            subtotal
                    );
                })
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(items, total);
    }
}