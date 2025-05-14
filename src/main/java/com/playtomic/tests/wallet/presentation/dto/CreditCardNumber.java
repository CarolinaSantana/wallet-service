package com.playtomic.tests.wallet.presentation.dto;

import com.playtomic.tests.wallet.presentation.exception.InvalidCreditCardNumberException;

public class CreditCardNumber {

    private static final String CREDIT_CARD_NUMBER_REGEX = "\\d{16}";

    private final String number;

    public CreditCardNumber(String number) {
        if (!number.matches(CREDIT_CARD_NUMBER_REGEX)) {
            throw new InvalidCreditCardNumberException("Invalid credit card number");
        }
        this.number = number;
    }

    public String masked() {
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    public String value() {
        return number;
    }
}
