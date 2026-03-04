package com.example.shopapp.order.repository;

import com.example.shopapp.order.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    List<OrderHistory> findByOrderIdOrderByCreatedAtAsc(Long orderId);
}
