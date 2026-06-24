package org.sqli.commonevents.event;

public record StockRejectedEvent(
        Long billId,
        Long productId,
        Integer quantity,
        String reason
) {
}