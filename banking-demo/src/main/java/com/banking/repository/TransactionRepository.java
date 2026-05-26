package com.banking.repository;

import com.banking.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Transaction Repository.
 *
 * KEY CONCEPTS:
 * 1. Idempotency lookup: findByIdempotencyKey is used BEFORE processing
 *    to detect duplicate requests and return the cached result.
 *
 * 2. Pagination: Always use Pageable for unbounded collections (transactions
 *    can grow to millions). Never return List<Transaction> for user-facing APIs.
 *
 * 3. @EntityGraph on paginated queries: avoids N+1 when each transaction
 *    page entry would trigger a separate SELECT for sourceAccount.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ── IDEMPOTENCY: look up by idempotency key ──────────────────────────────
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    // ── PAGINATION: never return unbounded lists ─────────────────────────────
    @EntityGraph(attributePaths = {"sourceAccount", "targetAccount"})
    Page<Transaction> findBySourceAccountId(Long accountId, Pageable pageable);

    @EntityGraph(attributePaths = {"sourceAccount", "targetAccount"})
    @Query("""
            SELECT t FROM Transaction t
            WHERE (t.sourceAccount.id = :accountId OR t.targetAccount.id = :accountId)
            AND t.createdAt BETWEEN :from AND :to
            """)
    Page<Transaction> findByAccountIdAndDateRange(
            @Param("accountId") Long accountId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);

    // ── Existence check for idempotency ──────────────────────────────────────
    boolean existsByIdempotencyKey(String idempotencyKey);

    // ── Count for metrics ────────────────────────────────────────────────────
    long countBySourceAccountIdAndStatus(Long accountId, Transaction.TransactionStatus status);
}
