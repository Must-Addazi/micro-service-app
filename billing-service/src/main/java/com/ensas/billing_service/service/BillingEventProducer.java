package com.ensas.billing_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.sqli.commonevents.event.BillCreatedEvent;

@Service
@RequiredArgsConstructor
public class BillingEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishBillCreated(BillCreatedEvent event) {

        kafkaTemplate.send(
                "bill-created",
                event.billId().toString(),
                event
        );
    }
}