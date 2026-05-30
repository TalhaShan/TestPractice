package domain;

import repo.TransactionRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryTransactionRepository
        implements TransactionRepository {

    private final CopyOnWriteArrayList<Transaction> transactions =
            new CopyOnWriteArrayList<>();

    private final ConcurrentHashMap<String, Boolean> processedIds =
            new ConcurrentHashMap<>();

    @Override
    public void save(Transaction transaction) {

        processedIds.put(transaction.transactionId(), true);

        transactions.add(transaction);
    }

    @Override
    public boolean exists(String transactionId) {
        return processedIds.containsKey(transactionId);
    }

    @Override
    public List<Transaction> findAll() {
        return transactions;
    }

    @Override
    public void remove(String transactionId) {
        processedIds.remove(transactionId);
    }
}
