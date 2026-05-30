package domain;

import repo.AccountRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccountRepository implements AccountRepository {

    private final ConcurrentHashMap<String, Account> storage =
            new ConcurrentHashMap<>();

    @Override
    public void save(Account account) {
        storage.put(account.getId(), account);
    }

    @Override
    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(storage.get(accountId));
    }
}
