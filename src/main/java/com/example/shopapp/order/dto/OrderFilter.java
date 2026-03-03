package com.example.shopapp.order.dto;

import com.example.shopapp.order.entity.OrderStatus;

import java.time.LocalDateTime;

public record OrderFilter(
        OrderStatus status,
        LocalDateTime from,
        LocalDateTime to
) {}