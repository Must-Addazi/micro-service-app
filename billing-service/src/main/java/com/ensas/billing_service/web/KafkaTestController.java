package com.ensas.billing_service.web;

import com.ensas.billing_service.service.BillingEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sqli.commonevents.event.BillCreatedEvent;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class KafkaTestController {

    private final BillingEventProducer producer;

    @PostMapping
    public String send() {

        producer.publishBillCreated(
                new BillCreatedEvent(
                        1L,
                        1L
                )
        );

        return "Event sent";
    }
}