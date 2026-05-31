package com.testcl.QueueBased;

import com.testcl.domain.Money;
import com.testcl.domain.TransactionRecord;
import com.testcl.enums.TransferResult;

import java.math.BigDecimal;
import java.util.*;

/**
 * Account variant for the actor/queue strategy.
 * All methods are intentionally NOT synchronised — correctness is
 * guaranteed by LedgerActor's single-thread ExecutorService.
 * Never call the "unsafe" methods from outside LedgerActor.
 */
public class AccountForActor {

    private final String   id;
    private final Currency currency;
    private       BigDecimal balance;   // guarded by actor thread
    private final List<TransactionRecord> history = new ArrayList<>();

    public AccountForActor(String id, BigDecimal initialBalance, Currency currency) {
        this.id       = Objects.requireNonNull(id);
        this.balance  = initialBalance;
        this.currency = currency;
    }

    // ─── Identity ──────────────────────────────────────────────────────────────

    /** ← ADDED: used by LedgerActor for self-transfer check. */
    public String   getId()      { return id; }          // ← ADDED
    public Currency getCurrency() { return currency; }

    // ─── "Unsafe" mutations (actor-thread only) ────────────────────────────────

    /**
     * ← ADDED: subtract amount without synchronisation.
     * Precondition: caller (LedgerActor) has already verified sufficient funds.
     * @return true always — fund check is the caller's responsibility.
     */
    public void withdrawUnsafe(BigDecimal amount) {        // ← ADDED
        balance = balance.subtract(amount);
        history.add(record("WITHDRAWAL", amount, TransferResult.SUCCESS));
    }

    /**
     * ← ADDED: add amount without synchronisation.
     * Safe to call because deposits never cause an invalid state — only
     * withdrawals can cause under-balance, and those are checked first.
     */
    public void depositUnsafe(BigDecimal amount) {         // ← ADDED
        balance = balance.add(amount);
        history.add(record("DEPOSIT", amount, TransferResult.SUCCESS));
    }

    // ─── Read (also actor-thread only for consistency) ─────────────────────────

    public BigDecimal getBalance() { return balance; }

    public List<TransactionRecord> getTransactionHistory() {
        return Collections.unmodifiableList(history);
    }

    // ─── Object contracts ──────────────────────────────────────────────────────

    @Override public boolean equals(Object o) {
        return o instanceof AccountForActor a && id.equals(a.id);
    }
    @Override public int    hashCode() { return id.hashCode(); }
    @Override public String toString() {
        return "Account[id=%s, balance=%s]".formatted(id, balance);
    }

    private TransactionRecord record(String type, BigDecimal amount, TransferResult result) {
        return new TransactionRecord(
            UUID.randomUUID(), id, null,
            new Money(amount, currency), result, java.time.Instant.now()
        );
    }
}
