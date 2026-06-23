package com.ensas.billing_service.feign;

import com.ensas.billing_service.config.FeignConfig;
import com.ensas.billing_service.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "inventory-service",
configuration = FeignConfig.class)
public interface ProductRestClient {
    @GetMapping("/products/{id}")
    Product getProductById(@PathVariable Long id);

    @GetMapping("/products")
    PagedModel<Product> getAllProducts();

    @PutMapping("/products/{id}/stock/decrease/{quantity}")
    Product decreaseStock(@PathVariable Long id, @PathVariable int quantity);
}
