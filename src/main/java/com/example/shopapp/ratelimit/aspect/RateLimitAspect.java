package com.example.shopapp.ratelimit.aspect;

import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.ratelimit.annotation.RateLimited;
import com.example.shopapp.ratelimit.service.RateLimitService;
import com.example.shopapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitService rateLimitService;

    @Before("@annotation(rateLimited)")
    public void checkRateLimit(RateLimited rateLimited) {

        Long userId = SecurityUtils.getCurrentUserId();

        String key = "rate:user:" + userId;

        boolean allowed = rateLimitService.isAllowed(
                key,
                rateLimited.limit(),
                rateLimited.windowSeconds()
        );

        if (!allowed) {
            throw new BadRequestException("Too many requests");
        }
    }
}
