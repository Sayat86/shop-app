package com.example.shopapp.order.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEmailListener {

    @EventListener
    public void handle(OrderCreatedEvent event) {

        System.out.println("Send email for order: " + event.orderNumber());

    }
}
