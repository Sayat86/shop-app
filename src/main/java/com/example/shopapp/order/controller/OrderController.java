package com.example.shopapp.order.controller;

import com.example.shopapp.order.dto.OrderFilter;
import com.example.shopapp.order.dto.OrderHistoryResponse;
import com.example.shopapp.order.dto.OrderResponse;
import com.example.shopapp.order.entity.OrderStatus;
import com.example.shopapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder() {

        OrderResponse response = service.createOrderFromCart();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.orderNumber())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        OrderFilter filter = new OrderFilter(status, from, to);

        return ResponseEntity.ok(
                service.getMyOrders(filter, pageable)
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.cancelOrder(id));
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable String orderNumber
    ) {
        return ResponseEntity.ok(service.getOrderByNumber(orderNumber));
    }

    @GetMapping("/{orderNumber}/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(
            @PathVariable String orderNumber
    ) {
        return ResponseEntity.ok(service.getOrderHistory(orderNumber));
    }
}