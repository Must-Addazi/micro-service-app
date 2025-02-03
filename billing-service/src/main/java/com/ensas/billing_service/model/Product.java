package com.ensas.billing_service.model;

import lombok.Data;

@Data
public class Product {

    private String id;
    private String name;
    private Double price;
    private int quantity;
}
