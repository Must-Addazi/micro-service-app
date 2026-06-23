package com.ensas.billing_service.dto;

public record AddCartItemRequest(Long productId, int quantity) {
}
