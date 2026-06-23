package com.ensas.billing_service.config;

import com.ensas.billing_service.execeptions.InventoryErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {

        return new InventoryErrorDecoder(
                objectMapper
        );
    }
}