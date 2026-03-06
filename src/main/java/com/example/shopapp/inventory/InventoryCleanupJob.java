package com.example.shopapp.inventory;

import com.example.shopapp.inventory.repository.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class InventoryCleanupJob {

    private final StockReservationRepository reservationRepository;

    @Scheduled(fixedDelay = 60000)
    public void cleanup() {

        reservationRepository.expireReservations();
    }
}
