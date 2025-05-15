package com.playtomic.tests.wallet.presentation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.playtomic.tests.wallet.presentation.exception.InvalidCreditCardNumberException;
import jakarta.validation.constraints.NotBlank;

public class CreditCardNumber {

    private static final String CREDIT_CARD_NUMBER_REGEX = "\\d{16}";

    @NotBlank
    private final String number;

    @JsonCreator
    public CreditCardNumber(String number) {
        if (!number.matches(CREDIT_CARD_NUMBER_REGEX)) {
            throw new InvalidCreditCardNumberException("Invalid credit card number");
        }
        this.number = number;
    }

    public String masked() {
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    @JsonValue
    public String value() {
        return number;
    }
}
