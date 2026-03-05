package com.example.shopapp.order.controller;

import com.example.shopapp.order.dto.CheckoutRequest;
import com.example.shopapp.order.dto.OrderResponse;
import com.example.shopapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse checkout(@RequestBody CheckoutRequest request) {
        return orderService.checkout(request);
    }
}
