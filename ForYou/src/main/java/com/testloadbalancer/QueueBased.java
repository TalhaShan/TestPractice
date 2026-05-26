package com.testloadbalancer;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QueueBased implements AutoCloseable{
//Don't forget to use Functions example CompateTo
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CompletableFuture<TransferResult> transfer(Account from, Account to, BigDecimal amount) {

        return CompletableFuture.supplyAsync(()->{
            if(from.getBalance().compareTo(amount)<0){  //Don't forget to use Functions
                return TransferResult.INSUFFICIENT_FUNDS;
            }
            from.withdraw(amount);
            to.deposit(amount);
            return TransferResult.SUCCESS;
        },executorService);
    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(5, TimeUnit.SECONDS)){
                executorService.shutdownNow();
            }
        }catch (InterruptedException e){
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}


