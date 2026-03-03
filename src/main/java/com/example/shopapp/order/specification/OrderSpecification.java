package com.example.shopapp.order.specification;

import com.example.shopapp.order.dto.OrderFilter;
import com.example.shopapp.order.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> withFilters(
            Long userId,
            OrderFilter filter
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // если userId != null — фильтруем по пользователю
            if (userId != null) {
                predicates.add(
                        cb.equal(root.get("user").get("id"), userId)
                );
            }

            if (filter.status() != null) {
                predicates.add(
                        cb.equal(root.get("status"), filter.status())
                );
            }

            if (filter.from() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                filter.from()
                        )
                );
            }

            if (filter.to() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                filter.to()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}