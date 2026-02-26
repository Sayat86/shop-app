package com.example.shopapp.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ApiError {
    private int status;
    private String message;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private Map<String, String> errors;
}
