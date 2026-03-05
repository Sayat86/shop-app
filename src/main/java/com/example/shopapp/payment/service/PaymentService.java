package com.example.shopapp.payment.service;

import com.example.shopapp.order.entity.Order;
import com.example.shopapp.order.entity.OrderStatus;
import com.example.shopapp.order.repository.OrderRepository;
import com.example.shopapp.payment.dto.PaymentResponse;
import com.example.shopapp.payment.entity.Payment;
import com.example.shopapp.payment.entity.PaymentStatus;
import com.example.shopapp.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentResponse createPayment(String orderNumber) {

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Order cannot be paid in status: " + order.getStatus());
        }

        paymentRepository.findByOrderOrderNumber(orderNumber)
                .ifPresent(p -> {
                    throw new RuntimeException("Payment already exists for this order");
                });

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .provider("FAKE_GATEWAY")
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        return mapToResponse(payment);
    }

    public PaymentResponse confirmPayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Payment already completed");
        }

        Order order = payment.getOrder();

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Order cannot be paid in status: " + order.getStatus());
        }

        payment.setStatus(PaymentStatus.SUCCESS);

        order.changeStatus(OrderStatus.PAID);

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getOrderNumber(),
                payment.getAmount(),
                payment.getStatus()
        );
    }

}
