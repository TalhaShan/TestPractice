package com.mine.built.it.unit.integration;

import com.mine.built.it.dto.CreateOrderRequest;
import com.mine.built.it.entity.Product;
import com.mine.built.it.repository.ProductRepository;
import com.mine.built.it.service.interfaces.OrderService;
import com.mine.built.it.unit.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ConcurrencyOrderIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldPreventOverselling() throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(10);

        CountDownLatch latch = new CountDownLatch(10);

        AtomicInteger success = new AtomicInteger();

        for (int i = 0; i < 10; i++) {

            executor.submit(() -> {

                try {

                    CreateOrderRequest request = TestData.orderRequest();

                    orderService.createOrder2(request);

                    success.incrementAndGet();

                } catch (Exception ignored) {

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Product product = productRepository.findById(1L).orElseThrow();

        assertThat(success.get()).isEqualTo(5);

        assertThat(product.getAvailableQuantity()).isEqualTo(0);
    }
}
