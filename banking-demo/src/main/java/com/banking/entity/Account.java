package com.banking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Account entity.
 *
 * KEY CONCURRENCY CONCEPT — @Version (Optimistic Locking):
 * - Hibernate adds "WHERE version = ?" to UPDATE statements
 * - If another thread updated first, version mismatch => OptimisticLockException
 * - Ideal for read-heavy workloads (no DB locks held during reads)
 * - Caller must catch OptimisticLockException and RETRY
 *
 * Compare with Pessimistic Locking (see AccountRepository):
 * - @Lock(PESSIMISTIC_WRITE) => SELECT FOR UPDATE
 * - Guarantees exclusive access, but holds DB lock => less scalable
 * - Use for write-heavy, short-duration critical sections
 */
@Entity
@Table(name = "accounts",
       indexes = {
           @Index(name = "idx_account_number", columnList = "account_number", unique = true),
           @Index(name = "idx_account_customer", columnList = "customer_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    /**
     * OPTIMISTIC LOCKING:
     * Every UPDATE includes "WHERE version = <current>" in SQL.
     * If two threads read version=5 and both try to update,
     * the second one finds version=6 and throws OptimisticLockException.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    /**
     * MANY-TO-ONE (owning side): Account stores customer_id FK column.
     * FetchType.LAZY avoids loading the full Customer graph on every Account query.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * ONE-TO-MANY to Transaction.
     * FetchType.LAZY is critical here — an account can have millions of transactions.
     */
    @OneToMany(mappedBy = "sourceAccount",
               cascade = CascadeType.PERSIST,
               fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> outgoingTransactions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Domain methods ───────────────────────────────────────────────────────

    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                "Insufficient funds. Balance: " + balance + ", Requested: " + amount);
        }
        this.balance = this.balance.subtract(amount);
    }

    public boolean isActive() {
        return AccountStatus.ACTIVE.equals(this.status);
    }

    public enum AccountType { CHECKING, SAVINGS, LOAN }
    public enum AccountStatus { ACTIVE, FROZEN, CLOSED }
}
