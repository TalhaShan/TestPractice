package com.testcl.lockbased;

import com.testcl.enums.TransferResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LedgerServiceLocked {
    // KEY INSIGHT: always acquire locks in consistent ID order to prevent deadlock
    public TransferResult transfer2(Account from, Account to, BigDecimal amount) {
        if (from.getId().equals(to.getId())) return TransferResult.SELF_TRANSFER;
        if (amount.compareTo(BigDecimal.ZERO) <= 0)  return TransferResult.INVALID_AMOUNT;

        // Order locks by ID — prevents deadlock with concurrent reverse transfers
        Account first  = from.getId().compareTo(to.getId()) < 0 ? from : to;
        Account second = first == from ? to : from;

        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (!from.withdraw(amount)) return TransferResult.INSUFFICIENT_FUNDS;
                to.deposit(amount);
                return TransferResult.SUCCESS;
            } finally { second.lock.unlock(); }
        } finally { first.lock.unlock(); }
    }



    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public void register(Account account) {
        accounts.putIfAbsent(account.getId(), account);  // ← getId() used here
    }

    public TransferResult transfer(Account from, Account to, BigDecimal amount) {

        // 1. Validate inputs ─────────────────────────────────────────────────
        if (amount == null || amount.signum() <= 0)
            return TransferResult.INVALID_AMOUNT;

        // 2. Self-transfer check — uses getId() ──────────────────────────────
        if (from.getId().equals(to.getId()))   // ← getId() used here
            return TransferResult.SELF_TRANSFER;

        // 3. Currency mismatch check — uses getCurrency() ────────────────────
        if (!from.getCurrency().equals(to.getCurrency()))
            return TransferResult.CURRENCY_MISMATCH;

        // 4. Deadlock-safe lock ordering — uses getId() ──────────────────────
        //    Always acquire the lock with the lexicographically smaller ID first.
        //    This prevents A→B and B→A from deadlocking each other.
        Account first  = from.getId().compareTo(to.getId()) < 0 ? from : to;
        Account second = (first == from) ? to : from;         // ← getId() x4 here

        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (!from.withdraw(amount))
                    return TransferResult.INSUFFICIENT_FUNDS;
                to.deposit(amount);
                return TransferResult.SUCCESS;
            } finally { second.lock.unlock(); }
        } finally { first.lock.unlock(); }
    }

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }
}

