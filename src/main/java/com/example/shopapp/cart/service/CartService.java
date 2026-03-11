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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String CART_PREFIX = "cart:user:";

    private String getCartKey(Long userId) {
        return CART_PREFIX + userId;
    }


    public CartResponse addToCart(AddToCartRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        ProductVariant variant = variantRepository.findById(request.variantId())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        if (variant.getStockQuantity() < request.quantity()) {
            throw new BadRequestException("Not enough stock available");
        }

        redisTemplate.opsForHash().increment(
                getCartKey(userId),
                request.variantId().toString(),
                request.quantity()
        );
        redisTemplate.expire(
                getCartKey(userId),
                Duration.ofHours(24)
        );

        return getCart();
    }


    public CartResponse updateQuantity(Long variantId, UpdateCartItemRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        if (variant.getStockQuantity() < request.quantity()) {
            throw new BadRequestException("Not enough stock available");
        }

        redisTemplate.opsForHash().put(
                getCartKey(userId),
                variantId.toString(),
                request.quantity().toString()
        );
        redisTemplate.expire(
                getCartKey(userId),
                Duration.ofHours(24)
        );

        return getCart();
    }


    public CartResponse removeFromCart(Long variantId) {

        Long userId = SecurityUtils.getCurrentUserId();

        redisTemplate.opsForHash()
                .delete(getCartKey(userId), variantId.toString());

        return getCart();
    }


    @Transactional(readOnly = true)
    public CartResponse getCart() {

        Long userId = SecurityUtils.getCurrentUserId();

        Map<Object, Object> entries =
                redisTemplate.opsForHash().entries(getCartKey(userId));

        if (entries.isEmpty()) {
            return new CartResponse(List.of(), BigDecimal.ZERO);
        }

        List<Long> variantIds = entries.keySet()
                .stream()
                .map(id -> Long.valueOf((String) id))
                .toList();

        List<ProductVariant> variants =
                variantRepository.findAllById(variantIds);

        Map<Long, ProductVariant> variantMap =
                variants.stream()
                        .collect(Collectors.toMap(
                                ProductVariant::getId,
                                v -> v
                        ));

        List<CartItemResponse> items = entries.entrySet()
                .stream()
                .map(entry -> {

                    Long variantId = Long.valueOf((String) entry.getKey());
                    Integer quantity = Integer.valueOf((String) entry.getValue());

                    ProductVariant variant = variantMap.get(variantId);

                    BigDecimal price = variant.getPrice();

                    BigDecimal subtotal =
                            price.multiply(BigDecimal.valueOf(quantity));

                    return new CartItemResponse(
                            null,
                            variantId,
                            variant.getSku(),
                            price,
                            quantity,
                            subtotal
                    );
                })
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(items, total);
    }


    public CartResponse clearCart() {

        Long userId = SecurityUtils.getCurrentUserId();

        redisTemplate.delete(getCartKey(userId));

        return new CartResponse(List.of(), BigDecimal.ZERO);
    }
}