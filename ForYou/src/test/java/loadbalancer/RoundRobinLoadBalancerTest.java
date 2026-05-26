package loadbalancer;

import concurrent.RoundRobinLoadBalancer;
import concurrent.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class RoundRobinLoadBalancerTest {

    private RoundRobinLoadBalancer loadBalancer;

    @BeforeEach
    void setUp() {
        loadBalancer = new RoundRobinLoadBalancer();
    }

    @AfterEach
    void tearDown() {
        loadBalancer.shutdown();
    }

    @Test
    void shouldRegisterServer() {

        loadBalancer.register(new Server("s1"));

        assertEquals(1, loadBalancer.size());
    }

    @Test
    void shouldNotRegisterDuplicateServer() {

        loadBalancer.register(new Server("s1"));
        loadBalancer.register(new Server("s1"));

        assertEquals(1, loadBalancer.size());
    }

    @Test
    void shouldRemoveServer() {

        loadBalancer.register(new Server("s1"));

        loadBalancer.remove("s1");

        assertEquals(0, loadBalancer.size());
    }

    @Test
    void shouldReturnServersInRoundRobinOrder() {

        loadBalancer.register(new Server("s1"));
        loadBalancer.register(new Server("s2"));
        loadBalancer.register(new Server("s3"));

        assertEquals("s1", loadBalancer.getNextServer().getId());
        assertEquals("s2", loadBalancer.getNextServer().getId());
        assertEquals("s3", loadBalancer.getNextServer().getId());
        assertEquals("s1", loadBalancer.getNextServer().getId());
    }

    @Test
    void shouldThrowExceptionWhenNoServersAvailable() {

        assertThrows(NoSuchElementException.class,
                () -> loadBalancer.getNextServer());
    }

    @Test
    void shouldSkipUnhealthyServers() {

        Server s1 = new Server("s1");
        Server s2 = new Server("s2");

        s1.setHealthy(false);

        loadBalancer.register(s1);
        loadBalancer.register(s2);

        assertEquals("s2", loadBalancer.getNextServer().getId());
    }

    @Test
    void shouldGetServerAsync() throws Exception {

        loadBalancer.register(new Server("s1"));

        CompletableFuture<Server> future =
                loadBalancer.getNextServerAsync();

        Server server = future.get(2, TimeUnit.SECONDS);

        assertEquals("s1", server.getId());
    }

    @Test
    void shouldHandleConcurrentRegistrations() throws Exception {

        int threads = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            int index = i;

            executor.submit(() -> {
                loadBalancer.register(new Server("s" + index));
                latch.countDown();
            });
        }

        latch.await();

        assertEquals(20, loadBalancer.size());

        executor.shutdown();
    }

    @Test
    void shouldHandleConcurrentRequestsSafely() throws Exception {

        loadBalancer.register(new Server("s1"));
        loadBalancer.register(new Server("s2"));
        loadBalancer.register(new Server("s3"));

        int totalRequests = 100;

        ExecutorService executor = Executors.newFixedThreadPool(20);

        CountDownLatch latch = new CountDownLatch(totalRequests);

        Set<String> results = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < totalRequests; i++) {
            executor.submit(() -> {
                Server server = loadBalancer.getNextServer();
                results.add(server.getId());
                latch.countDown();
            });
        }

        latch.await();

        assertEquals(3, results.size());

        executor.shutdown();
    }

    @RepeatedTest(5)
    void shouldBeThreadSafeUnderStress() throws Exception {

        loadBalancer.register(new Server("s1"));
        loadBalancer.register(new Server("s2"));
        loadBalancer.register(new Server("s3"));

        int requests = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(50);

        List<CompletableFuture<Server>> futures = new ArrayList<>();

        for (int i = 0; i < requests; i++) {

            CompletableFuture<Server> future =
                    CompletableFuture.supplyAsync(
                            loadBalancer::getNextServer,
                            executor
                    );

            futures.add(future);
        }

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        assertEquals(requests, futures.size());

        executor.shutdown();
    }

    @Test
    void shouldDistributeRequestsAlmostEvenly() {

        loadBalancer.register(new Server("s1"));
        loadBalancer.register(new Server("s2"));

        int s1Count = 0;
        int s2Count = 0;

        for (int i = 0; i < 100; i++) {
            Server server = loadBalancer.getNextServer();

            if (server.getId().equals("s1")) {
                s1Count++;
            } else {
                s2Count++;
            }
        }

        assertTrue(Math.abs(s1Count - s2Count) <= 1);
    }
}
