package com.example.demo.entities;

import com.example.demo.enums.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String imageUrl;

    private BigDecimal price;

    @Min(value = 0,message = "Quantity must be positive")
    private Integer quantity;

    private boolean active;
}
