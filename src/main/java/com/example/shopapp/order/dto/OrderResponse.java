package com.example.shopapp.order.dto;

import com.example.shopapp.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(

        String orderNumber,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {}