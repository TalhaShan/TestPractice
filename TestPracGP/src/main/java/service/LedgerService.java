package service;

import domain.Account;
import exceptions.AccountNotFoundException;
import exceptions.DuplicateTransactionException;
import domain.Money;
import domain.Transaction;
import enums.TransactionType;
import repo.AccountRepository;
import repo.TransactionRepository;

import java.time.Instant;

public class LedgerService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    public LedgerService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public void transfer(
            String transactionId,
            String fromAccountId,
            String toAccountId,
            Money amount
    ) {

        if (transactionRepository.exists(transactionId)) {
            throw new DuplicateTransactionException(transactionId);
        }

        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(fromAccountId));

        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(toAccountId));

        Account firstLock =
                from.getId().compareTo(to.getId()) < 0 ? from : to;

        Account secondLock =
                from.getId().compareTo(to.getId()) < 0 ? to : from;

        firstLock.getLock().lock();

        try {
            secondLock.getLock().lock();
            try {

                from.withdraw(amount);

                to.deposit(amount);

                Transaction transaction = new Transaction(
                        transactionId,
                        fromAccountId,
                        toAccountId,
                        amount,
                        TransactionType.TRANSFER,
                        Instant.now()
                );

                transactionRepository.save(transaction);

            } finally {
                secondLock.getLock().unlock();
            }

        } finally {
            firstLock.getLock().unlock();
          //  transactionRepository.remove(transactionId);
        }
    }
}
