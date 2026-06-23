package com.ensas.billing_service.dto;

import java.util.List;

public record CartResponse(List<CartItemResponse> items, int count, double total) {
}
