import com.testloadbalancer.Account;
import com.testloadbalancer.Service;
import com.testloadbalancer.TransferResult;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
public class TestAccounts {

//USE VAR
    private Account alice;
    private Account bob;
    private Service service;


    @BeforeEach
    void setup(){
        alice = new Account("1",bd(1000), Currency.getInstance("USD"));
        bob = new Account("2",bd(1000), Currency.getInstance("USD"));
        service = new Service();
    }

    @Test
    void successfulTransfer_updatesBalances(){
        var result = service.transfer(alice, bob, bd(100));
        assertThat(result).isEqualTo(TransferResult.SUCCESS);
        assertThat(alice.getBalance()).isEqualTo(bd(900));
        assertThat(bob.getBalance()).isEqualByComparingTo(bd(1100));
    }


    @Test
    void insufficientFunds_returnsInsufficientFunds(){
        var result = service.transfer(alice, bob, bd(10000));
        assertThat(result).isEqualTo(TransferResult.INSUFFICIENT_FUNDS);
        assertThat(alice.getBalance()).isEqualByComparingTo(bd(1000));

    }

    @Test
    void selfTransfer_returnsSelfTransfer(){
        var result = service.transfer(alice, alice, bd(100));
        assertThat(result).isEqualTo(TransferResult.SELF_TRANSFER);
        assertThat(alice.getBalance()).isEqualByComparingTo(bd(1000));
    }

    public BigDecimal bd(int amount){
        return BigDecimal.valueOf(amount);
    }

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
                    service.transfer(aliceToBob ? alice : bob,
                            aliceToBob ? bob : alice, bd(1));
            });
        }
        latch.countDown();  // release all threads at once
        pool.shutdown(); pool.awaitTermination(9, TimeUnit.SECONDS);

        BigDecimal totalAfter = alice.getBalance().add(bob.getBalance());
        // Conservation of money: total must be identical
        assertThat(totalAfter).isEqualByComparingTo(totalBefore);
    }


}
