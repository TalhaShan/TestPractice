package com.banking.dto;

import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.entity.Transaction;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTOs (Data Transfer Objects).
 *
 * BEST PRACTICES:
 * - NEVER expose entities directly in API responses (exposes internal structure,
 *   causes lazy-loading issues, circular references in JSON serialization).
 * - Separate Request DTOs (with validation) from Response DTOs (with formatting).
 * - Use records for immutable DTOs (Java 16+) — great for responses.
 * - Use classes with @Builder for complex request DTOs.
 */
public class BankingDtos {

    // ═══════════════════════════════════════════════════════════════════════
    // REQUEST DTOs
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Create customer request.
     * @NotBlank validates non-null AND non-whitespace.
     * @Email validates email format.
     * @Pattern validates custom regex.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateCustomerRequest {

        @NotBlank(message = "First name is required")
        @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
        private String firstName;

        @NotBlank(message = "Last name is required")
        @Size(min = 1, max = 100)
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
        private String phone;

        private String nationalId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateAccountRequest {

        @NotNull(message = "Customer ID is required")
        @Positive(message = "Customer ID must be positive")
        private Long customerId;

        @NotNull(message = "Account type is required")
        private Account.AccountType accountType;

        @DecimalMin(value = "0.00", message = "Initial deposit must be non-negative")
        @Builder.Default
        private BigDecimal initialDeposit = BigDecimal.ZERO;
    }

    /**
     * Transfer request.
     *
     * IDEMPOTENCY KEY:
     * The client generates this UUID before sending.
     * If the request fails mid-flight, the client retries with the SAME key.
     * The server detects the duplicate and returns the original result.
     * This prevents double-debits on network timeouts.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransferRequest {

        @NotBlank(message = "Idempotency key is required")
        @Size(min = 36, max = 64, message = "Idempotency key must be 36-64 characters (UUID recommended)")
        private String idempotencyKey;

        @NotBlank(message = "Source account number is required")
        private String sourceAccountNumber;

        @NotBlank(message = "Target account number is required")
        private String targetAccountNumber;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Transfer amount must be at least 0.01")
        @DecimalMax(value = "1000000.00", message = "Transfer amount cannot exceed 1,000,000")
        private BigDecimal amount;

        @Size(max = 500)
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepositRequest {

        @NotBlank(message = "Idempotency key is required")
        private String idempotencyKey;

        @NotBlank(message = "Account number is required")
        private String accountNumber;

        @NotNull
        @DecimalMin(value = "0.01")
        private BigDecimal amount;

        private String description;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // RESPONSE DTOs (Java Records — immutable, concise)
    // ═══════════════════════════════════════════════════════════════════════

    public record CustomerResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            Customer.CustomerStatus status,
            LocalDateTime createdAt
    ) {
        public static CustomerResponse from(Customer customer) {
            return new CustomerResponse(
                    customer.getId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getStatus(),
                    customer.getCreatedAt()
            );
        }
    }

    public record AccountResponse(
            Long id,
            String accountNumber,
            Account.AccountType accountType,
            BigDecimal balance,
            String currency,
            Account.AccountStatus status,
            LocalDateTime createdAt
    ) {
        public static AccountResponse from(Account account) {
            return new AccountResponse(
                    account.getId(),
                    account.getAccountNumber(),
                    account.getAccountType(),
                    account.getBalance(),
                    "USD",
                    account.getStatus(),
                    account.getCreatedAt()
            );
        }
    }

    public record TransactionResponse(
            Long id,
            String idempotencyKey,
            Transaction.TransactionType transactionType,
            BigDecimal amount,
            String currency,
            String sourceAccountNumber,
            String targetAccountNumber,
            Transaction.TransactionStatus status,
            String description,
            String failureReason,
            LocalDateTime createdAt,
            LocalDateTime completedAt
    ) {
        public static TransactionResponse from(Transaction txn) {
            return new TransactionResponse(
                    txn.getId(),
                    txn.getIdempotencyKey(),
                    txn.getTransactionType(),
                    txn.getAmount(),
                    txn.getCurrency(),
                    txn.getSourceAccount() != null ? txn.getSourceAccount().getAccountNumber() : null,
                    txn.getTargetAccount() != null ? txn.getTargetAccount().getAccountNumber() : null,
                    txn.getStatus(),
                    txn.getDescription(),
                    txn.getFailureReason(),
                    txn.getCreatedAt(),
                    txn.getCompletedAt()
            );
        }
    }

    // ── Standard API envelope ────────────────────────────────────────────────
    @Getter
    @Builder
    public static class ApiResponse<T> {
        private final boolean success;
        private final String message;
        private final T data;
        private final LocalDateTime timestamp;

        public static <T> ApiResponse<T> success(T data) {
            return ApiResponse.<T>builder()
                    .success(true)
                    .data(data)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        public static <T> ApiResponse<T> success(String message, T data) {
            return ApiResponse.<T>builder()
                    .success(true)
                    .message(message)
                    .data(data)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        public static <T> ApiResponse<T> error(String message) {
            return ApiResponse.<T>builder()
                    .success(false)
                    .message(message)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
}
