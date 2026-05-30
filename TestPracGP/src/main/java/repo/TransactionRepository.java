package repo;

import domain.Transaction;

import java.util.List;

public interface TransactionRepository {

    void save(Transaction transaction);

    boolean exists(String transactionId);

    List<Transaction> findAll();

    void remove(String transactionId);
}
