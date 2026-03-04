package com.example.shopapp.order.service;

import com.example.shopapp.cart.entity.Cart;
import com.example.shopapp.cart.entity.CartItem;
import com.example.shopapp.cart.repository.CartRepository;
import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.order.dto.OrderFilter;
import com.example.shopapp.order.dto.OrderHistoryResponse;
import com.example.shopapp.order.dto.OrderItemResponse;
import com.example.shopapp.order.dto.OrderResponse;
import com.example.shopapp.order.entity.*;
import com.example.shopapp.order.repository.OrderHistoryRepository;
import com.example.shopapp.order.repository.OrderRepository;
import com.example.shopapp.order.specification.OrderSpecification;
import com.example.shopapp.product.entity.Product;
import com.example.shopapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    public OrderResponse createOrderFromCart() {

        Long userId = SecurityUtils.getCurrentUserId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(cart.getUser())
                .status(OrderStatus.CREATED)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException(
                        "Not enough stock for product: " + product.getName()
                );
            }

            // уменьшаем stock
            product.setStockQuantity(
                    product.getStockQuantity() - cartItem.getQuantity()
            );

            BigDecimal price = product.getPrice();
            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(cartItem.getQuantity())
            );

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .price(price)          // snapshot
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build();

            order.getItems().add(orderItem);

            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);
        recordEvent(savedOrder, OrderEventType.ORDER_CREATED);

        // очищаем корзину
        cart.getItems().clear();

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(
            OrderFilter filter,
            Pageable pageable
    ) {

        return orderRepository
                .findAll(
                        OrderSpecification.withFilters(null, filter),
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(
            OrderFilter filter,
            Pageable pageable
    ) {

        Long userId = SecurityUtils.getCurrentUserId();

        return orderRepository
                .findAll(
                        OrderSpecification.withFilters(userId, filter),
                        pageable
                )
                .map(this::mapToResponse);
    }

    public OrderResponse cancelOrder(Long orderId) {

        Long userId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasRole("ROLE_ADMIN");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Если не админ — проверяем владельца
        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        // Если не админ — можно отменить только CREATED
        if (!isAdmin && order.getStatus() != OrderStatus.CREATED) {
            throw new BadRequestException("Order cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order already cancelled");
        }

        // Возвращаем stock
        restoreStock(order);

        order.changeStatus(OrderStatus.CANCELLED);

        recordEvent(order, OrderEventType.ORDER_CANCELLED);

        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {

        Order order = getByOrderNumber(orderNumber);

        Long userId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasRole("ROLE_ADMIN");

        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        return mapToResponse(order);
    }

    public OrderResponse markPaid(String orderNumber) {

        Order order = getByOrderNumber(orderNumber);

        order.changeStatus(OrderStatus.PAID);

        recordEvent(order, OrderEventType.ORDER_PAID);

        return mapToResponse(order);
    }

    public OrderResponse shipOrder(String orderNumber) {

        Order order = getByOrderNumber(orderNumber);

        order.changeStatus(OrderStatus.SHIPPED);

        recordEvent(order, OrderEventType.ORDER_SHIPPED);

        return mapToResponse(order);
    }

    public OrderResponse adminCancelOrder(String orderNumber) {

        Order order = getByOrderNumber(orderNumber);

        // вернуть stock
        restoreStock(order);

        order.changeStatus(OrderStatus.CANCELLED);

        recordEvent(order, OrderEventType.ORDER_CANCELLED);

        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getOrderHistory(String orderNumber) {

        Order order = getByOrderNumber(orderNumber);

        Long userId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasRole("ROLE_ADMIN");

        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        return orderHistoryRepository
                .findByOrderIdOrderByCreatedAtAsc(order.getId())
                .stream()
                .map(h -> new OrderHistoryResponse(
                        h.getEventType(),
                        h.getCreatedAt()
                ))
                .toList();
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                items,
                order.getCreatedAt()
        );
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Order getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private void recordEvent(Order order, OrderEventType eventType) {

        OrderHistory history = OrderHistory.builder()
                .order(order)
                .eventType(eventType)
                .build();

        orderHistoryRepository.save(history);
    }

    private void restoreStock(Order order) {

        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity()
            );
        }
    }
}
