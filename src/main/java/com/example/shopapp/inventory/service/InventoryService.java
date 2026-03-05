package com.example.shopapp.inventory.service;

import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.inventory.repository.StockReservationRepository;
import com.example.shopapp.inventory.reservation.ReservationStatus;
import com.example.shopapp.inventory.reservation.StockReservation;
import com.example.shopapp.product.variant.entity.ProductVariant;
import com.example.shopapp.product.variant.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final ProductVariantRepository variantRepository;
    private final StockReservationRepository reservationRepository;

    public void reserveStock(Long variantId, Integer quantity) {

        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        Integer reserved = reservationRepository.getReservedQuantity(variantId);

        int available = variant.getStockQuantity() - reserved;

        if (available < quantity) {
            throw new BadRequestException("Not enough stock");
        }

        StockReservation reservation = StockReservation.builder()
                .variant(variant)
                .quantity(quantity)
                .status(ReservationStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        reservationRepository.save(reservation);
    }
}
