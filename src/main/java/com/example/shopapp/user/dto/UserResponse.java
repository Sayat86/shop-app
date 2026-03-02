package com.example.shopapp.user.dto;

public record UserResponse(
        Long id,
        String email,
        String username,
        String role,
        boolean enabled
) {}
