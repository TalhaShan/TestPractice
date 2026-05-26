package com.testloadbalancer;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount cannot be negative");
    }
    public static Money of(long pence) {
        return new Money(BigDecimal.valueOf(pence), Currency.getInstance("GBP"));
    }
}
