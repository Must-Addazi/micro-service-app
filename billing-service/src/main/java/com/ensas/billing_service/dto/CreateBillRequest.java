package com.ensas.billing_service.dto;

import java.util.List;

public record CreateBillRequest(Long customerId, List<CreateBillItemRequest> items) {
}
