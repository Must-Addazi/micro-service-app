package com.ensas.billing_service.execeptions;

public class IllegalQuantityException extends RuntimeException {
    public IllegalQuantityException(String message) {
        super(message);
    }
}
