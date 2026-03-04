package com.example.shopapp.order.entity;

public enum OrderStatus {
    CREATED,
    PAID,
    SHIPPED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus target) {

        return switch (this) {

            case CREATED ->
                    target == PAID ||
                            target == CANCELLED;

            case PAID ->
                    target == SHIPPED ||
                            target == CANCELLED;

            case SHIPPED ->
                    false;

            case CANCELLED ->
                    false;
        };
    }
}
