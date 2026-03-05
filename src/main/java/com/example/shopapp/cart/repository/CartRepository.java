package com.example.shopapp.cart.repository;

import com.example.shopapp.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    @Query("""
    select c
    from Cart c
    left join fetch c.items i
    left join fetch i.variant v
    left join fetch v.product
    where c.user.id = :userId
""")
    Optional<Cart> findCartWithItems(Long userId);
}