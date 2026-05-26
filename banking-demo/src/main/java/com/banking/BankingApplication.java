package com.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Banking Demo Application
 *
 * Demonstrates:
 * - Concurrency control (Optimistic + Pessimistic Locking)
 * - Idempotency (via idempotency keys)
 * - JPA relationships (OneToMany, ManyToOne, ManyToMany)
 * - Transaction management
 * - RESTful API best practices
 * - AOP for cross-cutting concerns (logging, auditing)
 * - Exception handling
 * - Caching
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class BankingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}
