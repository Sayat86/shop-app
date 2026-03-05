package com.example.shopapp.order.event;

import com.example.shopapp.order.entity.Order;
import com.example.shopapp.order.entity.OrderEventType;
import com.example.shopapp.order.entity.OrderHistory;
import com.example.shopapp.order.repository.OrderHistoryRepository;
import com.example.shopapp.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderRepository orderRepository;

    @EventListener
    public void handle(OrderCreatedEvent event) {

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow();

        OrderHistory history = OrderHistory.builder()
                .order(order)
                .eventType(OrderEventType.ORDER_CREATED)
                .build();

        orderHistoryRepository.save(history);
    }
}
