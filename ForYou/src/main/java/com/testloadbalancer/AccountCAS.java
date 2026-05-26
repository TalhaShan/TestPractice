package com.testloadbalancer;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

//remner balance in pence and methods to withdraw and deposit
public final class AccountCAS {

    private final String id;
    private final AtomicLong balancePence;

    public AccountCAS(String id, long initialPence) {
        if (initialPence < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        this.id = Objects.requireNonNull(id);
        this.balancePence = new AtomicLong(initialPence);
    }

    public boolean withdraw(long pence) {
        validateAmount(pence);

        return balancePence.updateAndGet(current -> {
            if (current < pence) {
                return current; // insufficient funds
            }
            return current - pence;
        }) >= 0 && balancePence.get() >= 0;
    }

    public void deposit(long pence) {
        validateAmount(pence);
        balancePence.addAndGet(pence);
    }

    public long balancePence() {
        return balancePence.get();
    }

    public BigDecimal balance() {
        return BigDecimal.valueOf(balancePence())
                .movePointLeft(2);
    }

    public boolean withdraw2(long pence) {
        validateAmount(pence);
        return balancePence.compareAndSet(balancePence(), balancePence() - pence);
    }


    public String id() {
        return id;
    }

    private static void validateAmount(long pence) {
        if (pence <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
