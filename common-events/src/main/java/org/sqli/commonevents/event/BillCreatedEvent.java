package org.sqli.commonevents.event;

public record BillCreatedEvent(
        Long billId,
        Long productId,
        Integer quantity
) {
}