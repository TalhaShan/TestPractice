package com.testcl.QueueBased;

import com.testcl.enums.TransferResult;


import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Single-thread executor = actor model. All account mutations serialized.
public class LedgerActor implements AutoCloseable {
    private final ExecutorService executor =
        Executors.newSingleThreadExecutor();

    // Returns CompletableFuture — caller can wait or compose async
    public CompletableFuture<TransferResult> transfer(
            AccountForActor from, AccountForActor to, BigDecimal amount) {

        return CompletableFuture.supplyAsync(() -> {
            // Runs on single thread — no sync needed inside Account!
            if (from.getBalance().compareTo(amount) < 0)
                return TransferResult.INSUFFICIENT_FUNDS;
            from.withdrawUnsafe(amount);  // no lock — actor guarantees serial
            to.depositUnsafe(amount);
            return TransferResult.SUCCESS;
        }, executor);
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                executor.shutdownNow();
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
