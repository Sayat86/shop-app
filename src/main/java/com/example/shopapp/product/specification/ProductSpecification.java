package com.example.shopapp.product.specification;

import com.example.shopapp.product.dto.ProductFilter;
import com.example.shopapp.product.entity.Product;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> withFilters(ProductFilter filter) {

        return (root, query, cb) -> {

            // ✅ ВАЖНО: чтобы не ломать count query при пагинации
            if (query.getResultType() != Long.class) {
                root.fetch("category", JoinType.LEFT);
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.name() != null && !filter.name().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + filter.name().toLowerCase() + "%"
                        )
                );
            }

            if (filter.categoryId() != null) {
                predicates.add(
                        cb.equal(root.get("category").get("id"),
                                filter.categoryId())
                );
            }

            if (filter.status() != null) {
                predicates.add(
                        cb.equal(root.get("status"),
                                filter.status())
                );
            }

            if (filter.minPrice() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("price"),
                                filter.minPrice())
                );
            }

            if (filter.maxPrice() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("price"),
                                filter.maxPrice())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
