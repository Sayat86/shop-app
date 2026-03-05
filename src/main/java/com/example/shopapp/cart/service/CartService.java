package com.example.shopapp.cart.service;

import com.example.shopapp.cart.dto.AddToCartRequest;
import com.example.shopapp.cart.dto.CartItemResponse;
import com.example.shopapp.cart.dto.CartResponse;
import com.example.shopapp.cart.dto.UpdateCartItemRequest;
import com.example.shopapp.cart.entity.Cart;
import com.example.shopapp.cart.entity.CartItem;
import com.example.shopapp.cart.repository.CartItemRepository;
import com.example.shopapp.cart.repository.CartRepository;
import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.product.entity.Product;
import com.example.shopapp.product.repository.ProductRepository;
import com.example.shopapp.product.variant.entity.ProductVariant;
import com.example.shopapp.product.variant.repository.ProductVariantRepository;
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
    private final ProductVariantRepository variantRepository;
    private final CartItemRepository cartItemRepository;

    // ================= CORE METHOD =================

    public Cart getOrCreateCart() {

        Long userId = SecurityUtils.getCurrentUserId();

        return cartRepository.findCartWithItems(userId)
                .orElseGet(() -> createCart(userId));
    }

    public CartResponse addToCart(AddToCartRequest request) {

        Cart cart = getOrCreateCart();

        ProductVariant variant = variantRepository.findById(request.variantId())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        if (variant.getStockQuantity() < request.quantity()) {
            throw new BadRequestException("Not enough stock available");
        }

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndVariantId(cart.getId(), variant.getId());

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.quantity();

            if (variant.getStockQuantity() < newQuantity) {
                throw new BadRequestException("Not enough stock available");
            }

            item.setQuantity(newQuantity);

        } else {

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.quantity())
                    .build();

            cartItemRepository.save(newItem);
        }

        return getCart();
    }

    public CartResponse updateQuantity(Long variantId, UpdateCartItemRequest request) {

        Cart cart = getOrCreateCart();

        CartItem item = cartItemRepository
                .findByCartIdAndVariantId(cart.getId(), variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        ProductVariant variant = item.getVariant();

        if (variant.getStockQuantity() < request.quantity()) {
            throw new BadRequestException("Not enough stock available");
        }

        item.setQuantity(request.quantity());

        return getCart();
    }

    public CartResponse removeFromCart(Long variantId) {

        Cart cart = getOrCreateCart();

        CartItem item = cartItemRepository
                .findByCartIdAndVariantId(cart.getId(), variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        cartItemRepository.delete(item);

        return getCart();
    }

    @Transactional(readOnly = true)
    public CartResponse getCart() {

        Long userId = SecurityUtils.getCurrentUserId();

        return cartRepository.findCartWithItems(userId)
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

                    ProductVariant variant = item.getVariant();
                    Product product = variant.getProduct();

                    BigDecimal price = variant.getPrice();

                    BigDecimal subtotal = price.multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    );

                    return new CartItemResponse(
                            item.getId(),
                            variant.getId(),
                            variant.getSku(),
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