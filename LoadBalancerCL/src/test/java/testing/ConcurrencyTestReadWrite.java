package testing;

import com.testloadbalancer.ReadWriteLoadBalancer;
import com.testloadbalancer.RoundRobinSelect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrencyTestReadWrite {



    @Test void concurrentRoute_noExceptions() throws Exception {
        var lb = new ReadWriteLoadBalancer(new RoundRobinSelect());
        lb.register("s1"); lb.register("s2"); lb.register("s3");

        var executor = Executors.newFixedThreadPool(10);
        var latch = new CountDownLatch(10);
        var errors = new CopyOnWriteArrayList<Throwable>();

        for (int i=0; i<10; i++) {
            executor.submit(() -> {
                try {
                    for (int j=0; j<1000; j++) lb.route();
                } catch (Throwable t) { errors.add(t); }
                finally { latch.countDown(); }
            });
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        executor.shutdown();
        assertTrue(errors.isEmpty(), "Concurrent errors: " + errors);
    }
    /*
    1. Create 10 worker tasks
2. Each task calls lb.route() 1000 times
3. Each task calls latch.countDown() when done
4. Main thread waits using latch.await()
5. After all tasks finish, assert no errors happened
     */
}
