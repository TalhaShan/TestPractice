package com.banking.service;

import com.banking.dto.BankingDtos.*;
import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.exception.BankingException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Account Service.
 *
 * CONCEPTS:
 * - @Cacheable: cache frequently read account summaries — reduces DB load.
 * - @CacheEvict: invalidate cache on updates.
 * - readOnly transactions for all read operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found: " + request.getCustomerId()));

        if (customer.getStatus() != Customer.CustomerStatus.ACTIVE) {
            throw new BankingException("Cannot create account for non-active customer");
        }

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit())
                .customer(customer)
                .build();

        account = accountRepository.save(account);
        log.info("Account created. AccountNumber={}, Customer={}", account.getAccountNumber(), customer.getId());
        return AccountResponse.from(account);
    }

    /**
     * @Cacheable: result is cached in "accounts" cache by account number.
     * Subsequent calls with the same accountNumber skip the DB entirely.
     * @CacheEvict on updates ensures stale data is removed.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "accounts", key = "#accountNumber")
    public AccountResponse getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found: " + accountNumber));
        return AccountResponse.from(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found: " + customerId);
        }
        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance(Long customerId) {
        return accountRepository.sumBalanceByCustomerId(customerId)
                .orElse(BigDecimal.ZERO);
    }

    @Transactional
    @CacheEvict(value = "accounts", key = "#accountNumber")
    public AccountResponse freezeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        account.setStatus(Account.AccountStatus.FROZEN);
        return AccountResponse.from(accountRepository.save(account));
    }

    private String generateAccountNumber() {
        // In production, use a dedicated sequence or distributed ID generator
        String candidate;
        do {
            candidate = "ACC" + System.currentTimeMillis() +
                    UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        } while (accountRepository.existsByAccountNumber(candidate));
        return candidate;
    }
}
