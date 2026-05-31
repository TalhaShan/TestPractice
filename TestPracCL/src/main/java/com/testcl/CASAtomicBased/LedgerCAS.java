package com.testcl.CASAtomicBased;

import com.testcl.enums.TransferResult;

// Ledger — fixes Python's TOCTOU bug: deposit is now unconditional atomic add
public class LedgerCAS {
    public TransferResult transfer(AccountCAS from, AccountCAS to, long pence) {
        if (from.getId().equals(to.getId())) return TransferResult.SELF_TRANSFER;
        if (pence <= 0) return TransferResult.INVALID_AMOUNT;
        if (!from.withdraw(pence)) return TransferResult.INSUFFICIENT_FUNDS;
        to.deposit(pence);  // atomic addAndGet — no CAS loop needed
        return TransferResult.SUCCESS;
    }
}
