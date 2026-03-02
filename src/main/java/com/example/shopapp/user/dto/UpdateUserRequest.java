package com.example.shopapp.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Version is required")
        Long version
) {}
