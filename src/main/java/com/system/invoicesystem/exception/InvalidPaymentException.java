package com.system.invoicesystem.exception;

public class InvalidPaymentException extends RuntimeException{

    public InvalidPaymentException() {
    }

    public InvalidPaymentException(String message) {
        super(message);
    }

}
