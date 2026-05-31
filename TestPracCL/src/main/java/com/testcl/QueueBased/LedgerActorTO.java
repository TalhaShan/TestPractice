package com.testcl.QueueBased;

import com.testcl.enums.TransferResult;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LedgerActorTO implements AutoCloseable {

        private final ExecutorService executor =
                Executors.newSingleThreadExecutor(r -> {
                    Thread t = new Thread(r, "ledger-actor");
                    t.setDaemon(true);
                    return t;
                });

        public CompletableFuture<TransferResult> transfer(
                AccountForActor from, AccountForActor to, BigDecimal amount) {

            return CompletableFuture.supplyAsync(() -> {

                // All code here runs on the single actor thread — no sync needed
                if (from.getId().equals(to.getId()))     // ← getId() used here
                    return TransferResult.SELF_TRANSFER;

                if (amount == null || amount.signum() <= 0)
                    return TransferResult.INVALID_AMOUNT;

                if (from.getBalance().compareTo(amount) < 0)
                    return TransferResult.INSUFFICIENT_FUNDS;

                from.withdrawUnsafe(amount);  // ← withdrawUnsafe used here
                to.depositUnsafe(amount);     // ← depositUnsafe used here
                return TransferResult.SUCCESS;

            }, executor);
        }

        @Override
        public void close() throws InterruptedException {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                executor.shutdownNow();
        }
    }


