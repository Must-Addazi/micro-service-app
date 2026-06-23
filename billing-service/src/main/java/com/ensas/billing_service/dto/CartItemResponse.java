package com.ensas.billing_service.dto;

import com.ensas.billing_service.model.Product;

public record CartItemResponse(Long id, Long productId, int quantity, double unitPrice, double lineTotal, Product product) {
}
