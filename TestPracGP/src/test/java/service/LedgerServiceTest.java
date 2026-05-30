package service;


import domain.Account;
import domain.InMemoryAccountRepository;
import domain.InMemoryTransactionRepository;
import domain.Money;
import exceptions.DuplicateTransactionException;
import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

class LedgerServiceTest {

    private LedgerService ledgerService;

    private InMemoryAccountRepository accountRepository;

    @BeforeEach
    void setup() {

        accountRepository = new InMemoryAccountRepository();

        ledgerService = new LedgerService(
                accountRepository,
                new InMemoryTransactionRepository()
        );
    }

    @Test
    void shouldTransferMoneySuccessfully() {

        Account a = new Account("A", Money.of("100"));

        Account b = new Account("B", Money.of("50"));

        accountRepository.save(a);
        accountRepository.save(b);

        ledgerService.transfer(
                "tx1",
                "A",
                "B",
                Money.of("30")
        );

        assertThat(a.getBalance().value())
                .isEqualByComparingTo("70.00");

        assertThat(b.getBalance().value())
                .isEqualByComparingTo("80.00");
    }

    @Test
    void shouldRejectNegativeAmount() {

        Account a = new Account("A", Money.of("100"));

        Account b = new Account("B", Money.of("100"));

        accountRepository.save(a);
        accountRepository.save(b);

        assertThatThrownBy(() ->
                ledgerService.transfer(
                        "tx1",
                        "A",
                        "B",
                        Money.of("-10")
                )
        ).isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldPreventOverdraft() {

        Account a = new Account("A", Money.of("10"));

        Account b = new Account("B", Money.of("100"));

        accountRepository.save(a);
        accountRepository.save(b);

        assertThatThrownBy(() ->
                ledgerService.transfer(
                        "tx1",
                        "A",
                        "B",
                        Money.of("20")
                )
        ).isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void shouldRejectDuplicateTransaction() {

        Account a = new Account("A", Money.of("100"));

        Account b = new Account("B", Money.of("100"));

        accountRepository.save(a);
        accountRepository.save(b);

        ledgerService.transfer(
                "tx1",
                "A",
                "B",
                Money.of("10")
        );

        assertThatThrownBy(() ->
                ledgerService.transfer(
                        "tx1",
                        "A",
                        "B",
                        Money.of("10")
                )
        ).isInstanceOf(DuplicateTransactionException.class);
    }

    @Test
    void shouldRejectDuplicateTransaction2() {

        Account a = new Account("A", Money.of("100"));
        Account b = new Account("B", Money.of("100"));

        accountRepository.save(a);
        accountRepository.save(b);

        Money amount = Money.of("10");

        ledgerService.transfer(
                "tx1",
                "A",
                "B",
                amount
        );

        assertThatThrownBy(() ->
                ledgerService.transfer(
                        "tx1",
                        "A",
                        "B",
                        amount
                )
        ).isInstanceOf(DuplicateTransactionException.class);
    }

    @Test
    void shouldHandleConcurrentTransfersSafely()
            throws InterruptedException {

        Account a = new Account("A", Money.of("1000"));
        Account b = new Account("B", Money.of("1000"));

        accountRepository.save(a);
        accountRepository.save(b);

        ExecutorService executor =
                Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            int tx = i;
            executor.submit(() -> {

                ledgerService.transfer(
                        "tx-" + tx,
                        "A",
                        "B",
                        Money.of("1")
                );

                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
        assertThat(a.getBalance().value())
                .isEqualByComparingTo("900.00");
        assertThat(b.getBalance().value())
                .isEqualByComparingTo("1100.00");
    }

    @RepeatedTest(100)
    void shouldHandleConcurrentTransfersSafely2() throws InterruptedException {
        Account a = new Account("A", Money.of("1000"));
        Account b = new Account("B", Money.of("1000"));
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        accountRepository.save(a);
        accountRepository.save(b);
        ExecutorService executor =
                Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            int tx = i;
            executor.submit(() -> {
                try {

                    ledgerService.transfer(
                            "tx"+tx,
                            "A",
                            "B",
                            Money.of("100")
                    );
                    success.incrementAndGet();
                } catch (Exception e) {
                    failed.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();
        assertThat(a.getBalance().value()).isEqualByComparingTo("0.00");
        assertThat(success.get()).isEqualTo(10);
        assertThat(failed.get()).isEqualTo(90);
    }

}
