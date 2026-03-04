package com.example.shopapp.order.dto;

import com.example.shopapp.order.entity.OrderEventType;

import java.time.LocalDateTime;

public record OrderHistoryResponse(
        OrderEventType eventType,
        LocalDateTime createdAt
) {}
