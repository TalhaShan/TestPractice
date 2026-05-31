package LedgerService;

import com.testcl.enums.TransferResult;
import com.testcl.lockbased.Account;
import com.testcl.lockbased.LedgerServiceLocked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Ledger Transfer Tests")
class LedgerTest {

    private Account alice, bob;
    private LedgerServiceLocked ledger;

    @BeforeEach
    void setUp() {
        alice  = new Account("alice", bd(1000), Currency.getInstance("USD"));
        bob    = new Account("bob",   bd(500),Currency.getInstance("USD"));
        ledger = new LedgerServiceLocked();
    }

    // ---- Happy path ----
    @Test
    void successfulTransfer_updatesBalances() {
        var result = ledger.transfer(alice, bob, bd(200));
        assertThat(result).isEqualTo(TransferResult.SUCCESS);
        assertThat(alice.getBalance()).isEqualByComparingTo(bd(800));
        assertThat(bob.getBalance()).isEqualByComparingTo(bd(700));
    }

    // ---- Edge cases ----
    @Test
    void insufficientFunds_returnsResult_balancesUnchanged() {
        var result = ledger.transfer(alice, bob, bd(9999));
        assertThat(result).isEqualTo(TransferResult.INSUFFICIENT_FUNDS);
        assertThat(alice.getBalance()).isEqualByComparingTo(bd(1000)); // unchanged!
    }

    @Test
    void selfTransfer_isRejected() {
        assertThat(ledger.transfer(alice, alice, bd(100)))
            .isEqualTo(TransferResult.SELF_TRANSFER);
    }

    @Test
    void zeroAmount_isRejected() {
        assertThat(ledger.transfer(alice, bob, BigDecimal.ZERO))
            .isEqualTo(TransferResult.INVALID_AMOUNT);
    }

    // ---- Concurrency stress test ----
    @Test
    @Timeout(10)
    void concurrentTransfers_noMoneyCreatedOrDestroyed() throws InterruptedException {
        int threads = 20, transfersEach = 50;
        BigDecimal totalBefore = alice.getBalance().add(bob.getBalance());

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < threads; i++) {
            boolean aliceToBob = i % 2 == 0;
            pool.submit(() -> {
                try {
                    latch.await();  // all threads start simultaneously
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (int j = 0; j < transfersEach; j++)
                    ledger.transfer(aliceToBob ? alice : bob,
                                    aliceToBob ? bob : alice, bd(1));
            });
        }
        latch.countDown();  // release all threads at once
        pool.shutdown(); pool.awaitTermination(9, TimeUnit.SECONDS);

        BigDecimal totalAfter = alice.getBalance().add(bob.getBalance());
        // Conservation of money: total must be identical
        assertThat(totalAfter).isEqualByComparingTo(totalBefore);
    }

    private BigDecimal bd(long v) {
        return BigDecimal.valueOf(v);
    }
}
