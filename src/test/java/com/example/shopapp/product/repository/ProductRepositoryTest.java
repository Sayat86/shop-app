package com.example.shopapp.product.repository;

import com.example.shopapp.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    @Autowired
    private ProductRepository repository;

    @Test
    void shouldFindProductBySlug() {

        Product product = new Product();
        product.setName("iPhone");
        product.setSlug("iphone");

        repository.save(product);
        repository.flush();

        Optional<Product> result =
                repository.findBySlug("iphone");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("iPhone");
    }

    @Test
    void shouldReturnTrueWhenSlugExists() {

        Product product = new Product();
        product.setName("iPhone");
        product.setSlug("iphone");

        repository.save(product);
        repository.flush();

        boolean exists =
                repository.existsBySlug("iphone");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnPopularProducts() {

        Product p1 = new Product();
        p1.setName("iPhone");
        p1.setSlug("iphone");
        p1.setViews(100L);

        Product p2 = new Product();
        p2.setName("Samsung");
        p2.setSlug("samsung");
        p2.setViews(50L);

        repository.save(p1);
        repository.save(p2);
        repository.flush();

        Page<Product> result =
                repository.findByDeletedFalseOrderByViewsDesc(
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent().get(0).getViews())
                .isEqualTo(100);
    }
}
