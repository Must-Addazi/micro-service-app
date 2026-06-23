package com.example.demo.web;

import com.example.demo.entities.Product;
import com.example.demo.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductRestController {

    private final ProductService productService;

    @PutMapping("/{id}/stock/decrease/{quantity}")
    public ResponseEntity<Product> decreaseStock(@PathVariable Long id, @PathVariable int quantity) {
      Product product = productService.decreaseStock(id,quantity);
        return ResponseEntity.ok(product);
    }
}
