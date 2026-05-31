package com.testcl.CASAtomicBased;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class AccountCAS {
    private final String id;
    // Store pence as long — AtomicLong is lock-free via CPU CAS instruction
    private final AtomicLong balancePence;

    public AccountCAS(String id, long initialPence) {
        this.id = id;
        this.balancePence = new AtomicLong(initialPence);
    }

    public boolean withdraw(long pence) {
        while (true) {  // spin loop — fine under low-moderate contention
            long current = balancePence.get();
            if (current < pence) return false;
            if (balancePence.compareAndSet(current, current - pence)) return true;
            // CAS failed — another thread modified; retry
        }
    }

    public void deposit(long pence) {
        balancePence.addAndGet(pence);  // atomic, no loop needed for deposit
    }

    public BigDecimal getBalance() {
        return BigDecimal.valueOf(balancePence.get())
                         .movePointLeft(2);  // pence → pounds
    }

    public String getId() { return id; }
}
