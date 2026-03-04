package com.example.shopapp.order.controller;

import com.example.shopapp.order.dto.OrderFilter;
import com.example.shopapp.order.dto.OrderResponse;
import com.example.shopapp.order.entity.OrderStatus;
import com.example.shopapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{orderNumber}/pay")
    public ResponseEntity<OrderResponse> markPaid(
            @PathVariable String orderNumber) {

        return ResponseEntity.ok(service.markPaid(orderNumber));
    }

    @PostMapping("/{orderNumber}/ship")
    public ResponseEntity<OrderResponse> shipOrder(
            @PathVariable String orderNumber) {

        return ResponseEntity.ok(service.shipOrder(orderNumber));
    }

    @PostMapping("/{orderNumber}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable String orderNumber) {

        return ResponseEntity.ok(service.adminCancelOrder(orderNumber));
    }
}
