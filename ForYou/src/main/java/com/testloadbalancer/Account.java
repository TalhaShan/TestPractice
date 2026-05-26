package com.testloadbalancer;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

//BigDecimal has methods
//USER COMPARE TO
public class Account {
    private final String id;
    private BigDecimal balance;
    private final Currency currency;
    private final List<TransactionRecord> history = new CopyOnWriteArrayList<>();
     final ReentrantLock lock = new ReentrantLock();

    public Account(String id, BigDecimal balance, Currency currency) {
        this.id = id;
        this.balance = balance;
        this.currency = currency;
    }

    public void deposit(BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        lock.lock();
        try {
            balance = balance.add(amount);
        } finally {
            lock.unlock();
        }
    }

    public boolean withdraw(BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        lock.lock();
        try {
            if (balance.compareTo(amount) < 0) return false;
            balance = balance.subtract(amount);
            return true;
        } finally {
            lock.unlock();
        }
    }
    public String getId() {
        return id;
    }
    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
