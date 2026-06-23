package com.example.demo.services;

import com.example.demo.entities.Product;

public interface ProductService {
     Product decreaseStock(Long productId, int quantity);
}
