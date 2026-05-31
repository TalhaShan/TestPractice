package com.testcl.lockbased;

import com.testcl.domain.TransactionRecord;

import javax.annotation.concurrent.GuardedBy;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final String id;
    private BigDecimal balance;
    private final Currency currency;
    private final List<TransactionRecord> history = new CopyOnWriteArrayList<>();
    final ReentrantLock lock = new ReentrantLock();

    public Account(String id, BigDecimal initialBalance, Currency currency) {
        this.id = id;
        this.balance = initialBalance;
        this.currency = currency;
    }

    @GuardedBy("lock")
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Deposit must be positive");
        lock.lock();
        try {
            balance = balance.add(amount);
        } finally { lock.unlock(); }
    }

    @GuardedBy("lock")
    public boolean withdraw(BigDecimal amount) {
        lock.lock();
        try {
            if (balance.compareTo(amount) < 0) return false;
            balance = balance.subtract(amount);
            return true;
        } finally { lock.unlock(); }
    }

    public BigDecimal getBalance() {
        lock.lock();
        try { return balance; }
        finally { lock.unlock(); }
    }

    public String getId() { return id; }

    public Currency getCurrency() { return currency; }

}
