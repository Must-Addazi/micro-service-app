package com.ensas.billing_service.model;

import lombok.Data;

@Data
public class Product {

    private Long id;
    private String name;
    private String description;
    private String category;
    private String imageUrl;
    private Double price;
    private int quantity;
}
