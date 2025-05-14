package com.playtomic.tests.wallet.presentation.exception;

public class InvalidCreditCardNumberException extends RuntimeException {
    public InvalidCreditCardNumberException(String message) {
        super(message);
    }
}
