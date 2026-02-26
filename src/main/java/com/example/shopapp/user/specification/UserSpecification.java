package com.example.shopapp.user.specification;

import com.example.shopapp.user.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            if (username == null || username.isEmpty()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + username.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.isEmpty()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<User> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("deleted"));
    }
}
