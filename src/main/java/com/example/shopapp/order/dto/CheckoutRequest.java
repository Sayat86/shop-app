package com.example.shopapp.order.dto;

public record CheckoutRequest(

        String email,
        String phone,
        String address,
        String city,
        String postalCode

) {}
