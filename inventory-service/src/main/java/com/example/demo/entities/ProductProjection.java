package com.example.demo.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "all",types = Product.class)
public interface ProductProjection {
    String getName();
    Double getPrice();

}
