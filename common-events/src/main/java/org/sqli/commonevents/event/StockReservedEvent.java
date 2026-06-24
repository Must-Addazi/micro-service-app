package org.sqli.commonevents.event;

public record StockReservedEvent(
        Long billId,
        Long productId,
        Integer quantity
) {
}