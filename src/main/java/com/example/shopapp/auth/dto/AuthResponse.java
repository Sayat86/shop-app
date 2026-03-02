package com.example.shopapp.auth.dto;

public record AuthResponse(
        Long userId,
        String email,
        String role,
        String accessToken,
        String refreshToken
) {}
