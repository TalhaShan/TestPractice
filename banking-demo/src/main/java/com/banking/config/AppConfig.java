package com.banking.config;

import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.math.BigDecimal;

/**
 * Application Configuration.
 *
 * @EnableRetry: activates Spring Retry for @Retryable annotations.
 * Without this, @Retryable does nothing.
 *
 * CommandLineRunner: runs after application context is ready.
 * Used here to seed demo data — great for showcasing in interviews.
 */
@Configuration
@EnableRetry   // REQUIRED for @Retryable in TransactionService to work
@RequiredArgsConstructor
@Slf4j
public class AppConfig {

    @Bean
    public CommandLineRunner seedDemoData(
            CustomerRepository customerRepository,
            AccountRepository accountRepository) {

        return args -> {
            log.info("Seeding demo data...");

            // Create customers
            Customer alice = Customer.builder()
                    .firstName("Alice").lastName("Johnson")
                    .email("alice@bank.com").phone("+15551234567")
                    .nationalId("NID-001").build();

            Customer bob = Customer.builder()
                    .firstName("Bob").lastName("Smith")
                    .email("bob@bank.com").phone("+15559876543")
                    .nationalId("NID-002").build();

            alice = customerRepository.save(alice);
            bob = customerRepository.save(bob);

            // Create accounts
            Account aliceChecking = Account.builder()
                    .accountNumber("ACC-ALICE-001")
                    .accountType(Account.AccountType.CHECKING)
                    .balance(new BigDecimal("5000.00"))
                    .customer(alice).build();

            Account aliceSavings = Account.builder()
                    .accountNumber("ACC-ALICE-002")
                    .accountType(Account.AccountType.SAVINGS)
                    .balance(new BigDecimal("20000.00"))
                    .customer(alice).build();

            Account bobChecking = Account.builder()
                    .accountNumber("ACC-BOB-001")
                    .accountType(Account.AccountType.CHECKING)
                    .balance(new BigDecimal("3000.00"))
                    .customer(bob).build();

            accountRepository.save(aliceChecking);
            accountRepository.save(aliceSavings);
            accountRepository.save(bobChecking);

            log.info("Demo data seeded: 2 customers, 3 accounts");
            log.info("H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:bankingdb)");
            log.info("API Base: http://localhost:8080/api/v1");
        };
    }
}
