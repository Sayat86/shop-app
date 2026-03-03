package com.example.shopapp.order.controller;

import com.example.shopapp.order.dto.OrderFilter;
import com.example.shopapp.order.dto.OrderResponse;
import com.example.shopapp.order.entity.OrderStatus;
import com.example.shopapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService service;

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            Pageable pageable
    ) {

        OrderFilter filter = new OrderFilter(status, from, to);

        return ResponseEntity.ok(
                service.getAllOrders(filter, pageable)
        );
    }
}
