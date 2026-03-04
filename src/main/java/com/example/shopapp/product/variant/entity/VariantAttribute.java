package com.example.shopapp.product.variant.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "variant_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
}
