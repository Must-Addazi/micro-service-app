package com.example.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.sqli.commonevents.event.BillCreatedEvent;

@Service
@Slf4j
public class InventoryKafkaConsumer {

    @KafkaListener(
            topics = "bill-created",
            groupId = "inventory-group"
    )
    public void consume(
            BillCreatedEvent event
    ) {

        log.info(
                "Received bill {} for customer {}",
                event.billId(),
                event.customerId()
        );
    }
}