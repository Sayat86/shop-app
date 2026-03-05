package com.example.shopapp.order.event;

public record OrderCreatedEvent(
        Long orderId,
        String orderNumber
) {}
