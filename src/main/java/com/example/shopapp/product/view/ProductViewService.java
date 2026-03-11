package com.example.shopapp.product.view;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductViewService {

    private final StringRedisTemplate redisTemplate;

    private static final String VIEW_KEY_PREFIX = "product:views:";
    private static final String POPULAR_PRODUCTS_KEY = "product:popular";

    public void incrementView(Long productId) {

        redisTemplate.opsForValue()
                .increment(VIEW_KEY_PREFIX + productId);

        redisTemplate.opsForZSet()
                .incrementScore(POPULAR_PRODUCTS_KEY, productId.toString(), 1);
    }

    public Long getViews(Long productId) {

        String value = redisTemplate.opsForValue()
                .get(VIEW_KEY_PREFIX + productId);

        return value == null ? 0L : Long.parseLong(value);
    }

    public List<Long> getTopProducts(int limit) {

        Set<String> ids = redisTemplate.opsForZSet()
                .reverseRange(POPULAR_PRODUCTS_KEY, 0, limit - 1);

        if (ids == null) {
            return List.of();
        }

        return ids.stream()
                .map(Long::valueOf)
                .toList();
    }
}
