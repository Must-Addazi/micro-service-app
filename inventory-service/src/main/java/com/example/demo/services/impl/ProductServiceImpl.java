package com.example.demo.services.impl;


import com.example.demo.entities.Product;
import com.example.demo.exceptions.IllegalQuantityException;
import com.example.demo.exceptions.InsufficientStockException;
import com.example.demo.exceptions.ProductNotFoundException;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Product decreaseStock(Long productId, int quantity) {
        if (quantity < 1){
            throw new IllegalQuantityException("Quantity must be more than 0");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id : " + productId));
        if(product.getQuantity() < quantity){
            throw new InsufficientStockException(
                    "Insufficient stock for product " + productId);        }
       return productRepository.save(product);
    }
}
