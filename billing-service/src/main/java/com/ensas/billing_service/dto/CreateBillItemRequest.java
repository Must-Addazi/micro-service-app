package com.ensas.billing_service.dto;

public record CreateBillItemRequest(Long productId, int quantity) {
}
