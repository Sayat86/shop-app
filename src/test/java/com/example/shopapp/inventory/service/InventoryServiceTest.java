package com.example.shopapp.inventory.service;

import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.inventory.repository.StockReservationRepository;
import com.example.shopapp.product.variant.entity.ProductVariant;
import com.example.shopapp.product.variant.repository.ProductVariantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductVariantRepository variantRepository;

    @Mock
    private StockReservationRepository reservationRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void shouldReserveStock() {

        ProductVariant variant = new ProductVariant();
        variant.setId(1L);
        variant.setStockQuantity(10);

        when(variantRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(variant));

        when(reservationRepository.getReservedQuantity(1L))
                .thenReturn(0);

        inventoryService.reserveStock(1L, 2);

        verify(reservationRepository).save(any());
    }

    @Test
    void shouldThrowWhenStockNotEnough() {

        ProductVariant variant = new ProductVariant();
        variant.setId(1L);
        variant.setStockQuantity(10);

        when(variantRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(variant));

        when(reservationRepository.getReservedQuantity(1L))
                .thenReturn(9);

        assertThrows(
                BadRequestException.class,
                () -> inventoryService.reserveStock(1L, 2)
        );
    }
}
