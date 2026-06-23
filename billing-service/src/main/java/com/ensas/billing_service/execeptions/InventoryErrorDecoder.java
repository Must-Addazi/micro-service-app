package com.ensas.billing_service.execeptions;

import com.ensas.billing_service.dto.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {

        try {

            ApiError error = objectMapper.readValue(
                    response.body().asInputStream(),
                    ApiError.class
            );

            return switch (response.status()) {

                case 404 ->
                        new NotFoundException(error.message());

                case 409 ->
                        new InsufficientStockException(error.message());

                default ->
                        new RuntimeException(error.message());
            };

        } catch (Exception e) {

            return new RuntimeException(
                    "Inventory service unavailable"
            );
        }
    }
}