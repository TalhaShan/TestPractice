package com.testloadbalancer;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Service {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public void register(Account account){
        accounts.putIfAbsent(account.getId(), account);
    }

    public TransferResult transfer(Account from, Account to, BigDecimal amount) {

        if (amount == null || amount.signum() <= 0) {
            return TransferResult.INVALID_AMOUNT;
        }

        if (from.getId().equals(to.getId())) {
            return TransferResult.SELF_TRANSFER;
        }

        if (from.getCurrency() != to.getCurrency()) {
            return TransferResult.CURRENCY_MISMATCH;
        }

        Account first = from.getId().compareTo(to.getId()) < 0 ? from : to;
        Account second = (first == from) ? to : from;

        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (!from.withdraw(amount)) {
                    return TransferResult.INSUFFICIENT_FUNDS;
                }
                to.deposit(amount);
                return TransferResult.SUCCESS;
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }
    }
