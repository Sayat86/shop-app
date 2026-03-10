package com.example.shopapp.inventory.repository;

import com.example.shopapp.product.variant.entity.ProductVariant;
import com.example.shopapp.testdata.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class StockReservationRepositoryTest {

    @Autowired
    private StockReservationRepository reservationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldReturnReservedQuantity() {

        ProductVariant variant = TestDataFactory.variant(entityManager);

        TestDataFactory.reservation(entityManager, variant, 3);
        entityManager.flush();

        Integer reserved = reservationRepository.getReservedQuantity(variant.getId());

        assertEquals(3, reserved);
    }
}
