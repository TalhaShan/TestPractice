package com.banking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction entity.
 *
 * KEY CONCEPTS:
 * 1. idempotencyKey: unique constraint ensures duplicate requests are detected at DB level.
 *    If the same key is submitted twice, the second INSERT fails with a constraint violation,
 *    and the service layer returns the ORIGINAL response — safe for retries.
 *
 * 2. ManyToOne to Account (source & destination): a single transaction links two accounts.
 *    Using separate @JoinColumn for each — clean schema, no join table needed.
 *
 * 3. Immutability: transactions should NEVER be updated after creation.
 *    Use @Column(updatable = false) on all financial fields.
 */
@Entity
@Table(name = "transactions",
       indexes = {
           @Index(name = "idx_txn_idempotency_key", columnList = "idempotency_key", unique = true),
           @Index(name = "idx_txn_source_account", columnList = "source_account_id"),
           @Index(name = "idx_txn_target_account", columnList = "target_account_id"),
           @Index(name = "idx_txn_created_at", columnList = "created_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * IDEMPOTENCY KEY:
     * Client generates a UUID per logical operation.
     * DB unique constraint prevents double-processing.
     * If same key arrives again, we return the stored result.
     */
    @Column(name = "idempotency_key", nullable = false, unique = true, updatable = false, length = 64)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, updatable = false)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3, updatable = false)
    @Builder.Default
    private String currency = "USD";

    /**
     * SOURCE ACCOUNT — ManyToOne, optional=false means NOT NULL in DB.
     * LAZY fetch: loading a transaction does not automatically load the full Account.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false, updatable = false)
    private Account sourceAccount;

    /**
     * TARGET ACCOUNT — nullable for DEPOSIT transactions (no source peer).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id", updatable = false)
    private Account targetAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // ── Status transitions ───────────────────────────────────────────────────

    public void markCompleted() {
        this.status = TransactionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String reason) {
        this.status = TransactionStatus.FAILED;
        this.failureReason = reason;
        this.completedAt = LocalDateTime.now();
    }

    public enum TransactionType { DEPOSIT, WITHDRAWAL, TRANSFER }
    public enum TransactionStatus { PENDING, COMPLETED, FAILED, REVERSED }
}
