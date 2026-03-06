package com.example.shopapp.inventory.repository;

import com.example.shopapp.inventory.reservation.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StockReservationRepository
        extends JpaRepository<StockReservation, Long> {

    @Query("""
           select coalesce(sum(r.quantity),0)
           from StockReservation r
           where r.variant.id = :variantId
           and r.status = 'ACTIVE'
           and r.expiresAt > CURRENT_TIMESTAMP
           """)
    Integer getReservedQuantity(Long variantId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
       update StockReservation r
       set r.status = 'EXPIRED'
       where r.status = 'ACTIVE'
       and r.expiresAt < CURRENT_TIMESTAMP
       """)
    void expireReservations();
}
